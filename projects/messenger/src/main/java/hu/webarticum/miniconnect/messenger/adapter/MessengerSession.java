package hu.webarticum.miniconnect.messenger.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredLargeDataSaveResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.request.SessionCloseRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionCloseResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;
import hu.webarticum.miniconnect.messenger.util.OrderAligningQueue;

public class MessengerSession implements MiniSession {

    // TODO: make these configurable
    private static final String SQLSTATE_CONNECTIONERROR = "08006";
    
    private static final int DATA_SEND_CHUNK_SIZE = 4096;
    
    private static final int RESULT_TIMEOUT_VALUE = 60;
    
    private static final TimeUnit RESULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    
    private final Messenger messenger;
    
    private final CompletableFuture<Long> sessionIdFuture = new CompletableFuture<>();
    
    private final AtomicInteger exchangeIdCounter = new AtomicInteger();
    
    private volatile boolean closed = false;

    
    public MessengerSession(Messenger messenger) {
        this.messenger = messenger;
        loadSessionIdAsync(messenger, sessionIdFuture);
    }

    private static void loadSessionIdAsync(Messenger messenger, CompletableFuture<Long> future) {
        new Thread(() -> loadSessionId(messenger, future)).start();
    }
    
    private static void loadSessionId(Messenger messenger, CompletableFuture<Long> future) {
        Consumer<Response> responseConsumer = r -> acceptSessionInitResponse(r, future);
        messenger.accept(new SessionInitRequest(), responseConsumer);
        waitForFutureSilently(future);
        new Blackhole().consume(responseConsumer);
    }
    
    private static void acceptSessionInitResponse(Response response, CompletableFuture<Long> future) {
        if (!(response instanceof SessionInitResponse)) {
            return;
        }
        
        SessionInitResponse sessionInitResponse = (SessionInitResponse) response;
        // TODO: store the entire response and handle potential errors
        future.complete(sessionInitResponse.sessionId());
    }


    public long sessionId() {
        try {
            return sessionIdFuture.get(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("No session id was obtained", e);
        } catch (Exception e) {
            throw new IllegalStateException("No session id was obtained", e);
        }
    }
    
    @Override
    public MiniResult execute(String query) {
        int exchangeId = exchangeIdCounter.incrementAndGet();
        
        OrderAligningQueue<Response> responseQueue = new OrderAligningQueue<>(
                MessengerSession::checkNextResultResponse);

        CompletableFuture<MessengerResultSetCharger> resultSetFuture = new CompletableFuture<>();
        
        long sessionId = sessionId();
        QueryRequest queryRequest = new QueryRequest(sessionId, exchangeId, query);
        
        Consumer<Response> responseConsumer =
                r -> receiveResponse(r, resultSetFuture, responseQueue);
        messenger.accept(queryRequest, responseConsumer);

        Response firstResponse;
        try {
            firstResponse = responseQueue.take(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            resultSetFuture.cancel(true);
            return new StoredResult(new StoredError(
                    1, SQLSTATE_CONNECTIONERROR, "Interrupt occured while waiting for results"));
        } catch (TimeoutException e) {
            resultSetFuture.cancel(true);
            return new StoredResult(new StoredError(
                    2, SQLSTATE_CONNECTIONERROR, "Timeout reached while waiting for results"));
        }
        
        if (!(firstResponse instanceof ResultResponse)) {
            resultSetFuture.cancel(true);
            return new StoredResult(new StoredError(3, SQLSTATE_CONNECTIONERROR, "Bad response"));
        }

        ResultResponse resultResponse = (ResultResponse) firstResponse;
        if (!resultResponse.success()) {
            resultSetFuture.cancel(true);
            ResultResponse.ErrorData errorData = resultResponse.error();
            return new StoredResult(new StoredError(
                    errorData.code(),
                    errorData.sqlState(),
                    errorData.message()));
        }
        
        MessengerResultSetCharger resultSet =
                new MessengerResultSetCharger(resultResponse, responseConsumer);
        resultSetFuture.complete(resultSet);
        new Thread(() -> pollResponseQueue(responseQueue, resultSet)).start();
        
        return new MessengerResult(resultResponse, resultSet);
    }
    
    private void receiveResponse(
            Response response,
            CompletableFuture<MessengerResultSetCharger> resultSetFuture,
            OrderAligningQueue<Response> responseQueue) {
        if (response instanceof ResultSetValuePartResponse) {
            ResultSetValuePartResponse partResponse = (ResultSetValuePartResponse) response;
            resultSetFuture.thenAcceptAsync(resultSet -> resultSet.acceptPart(partResponse));
        } else {
            responseQueue.add(response);
        }
    }
    
    // TODO: error handling
    private void pollResponseQueue(OrderAligningQueue<Response> responseQueue, MessengerResultSetCharger resultSet) {
        while (fetchResponseQueue(responseQueue, resultSet)) {
            // nothing to do
        }
    }
        
    private boolean fetchResponseQueue(OrderAligningQueue<Response> responseQueue, MessengerResultSetCharger resultSet) {
        Response response;
        try {
            response = responseQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        if (response instanceof ResultSetRowsResponse) {
            resultSet.accept((ResultSetRowsResponse) response);
        } else if (response instanceof ResultSetEofResponse) {
            resultSet.acceptEof();
            return false;
        }

        return true;
    }
    
    private static boolean checkNextResultResponse(Response previousResponse, Response response) {
        if (response instanceof ResultResponse) {
            return previousResponse == null;
        } else if (response instanceof ResultSetRowsResponse) {
            ResultSetRowsResponse resultSetRowsResponse = (ResultSetRowsResponse) response;
            long rowOffset = resultSetRowsResponse.rowOffset();
            return checkNextOffset(previousResponse, rowOffset);
        } else if (response instanceof ResultSetEofResponse) {
            ResultSetEofResponse resultSetEofResponse = (ResultSetEofResponse) response;
            long endOffset = resultSetEofResponse.endOffset();
            return checkNextOffset(previousResponse, endOffset);
        } else {
            return false;
        }
    }
    
    private static boolean checkNextOffset(Response previousResponse, long nextOffset) {
        if (nextOffset == 0L) {
            return true;
        }
        if (!(previousResponse instanceof ResultSetRowsResponse)) {
            return false;
        }
        
        ResultSetRowsResponse previousResultSetRowsResponse =
                (ResultSetRowsResponse) previousResponse;
        long previousRowOffset = previousResultSetRowsResponse.rowOffset();
        int previousRowCount = previousResultSetRowsResponse.rows().size();
        long previousEndOffset = previousRowOffset + previousRowCount;
        
        return nextOffset == previousEndOffset;
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(String variableName, long length, InputStream dataSource) {
        if (closed) {
            return new StoredLargeDataSaveResult(
                    false, new StoredError(6, SQLSTATE_CONNECTIONERROR, "Closed connection"));
        }
        
        int exchangeId = exchangeIdCounter.incrementAndGet();
        
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        Consumer<Response> responseConsumer = responseFuture::complete;

        long sessionId = sessionId();
        LargeDataHeadRequest largeDataHeadRequest =
                new LargeDataHeadRequest(sessionId, exchangeId, variableName, length);
        messenger.accept(largeDataHeadRequest, responseConsumer);

        byte[] buffer = new byte[DATA_SEND_CHUNK_SIZE];
        int readSize = 0;
        long offset = 0;
        while ((readSize = readStream(dataSource, buffer)) != -1) {
            // TODO: check for error
            ByteString content = ByteString.wrap(Arrays.copyOf(buffer, readSize));
            LargeDataPartRequest largeDataPartRequest =
                    new LargeDataPartRequest(sessionId, exchangeId, offset, content);
            messenger.accept(largeDataPartRequest);
            offset += readSize;
        }
        
        // we must be sure that responseConsumer is reachable until this point
        new Blackhole().consume(responseConsumer);
        
        Response response = null;
        try {
            response = responseFuture.get(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            // nothing to do
        }

        if (response instanceof LargeDataSaveResponse) {
            LargeDataSaveResponse largeDataSaveResponse = (LargeDataSaveResponse) response;
            return new StoredLargeDataSaveResult(
                    largeDataSaveResponse.success(),
                    new StoredError(
                        largeDataSaveResponse.errorCode(),
                        largeDataSaveResponse.sqlState(),
                        largeDataSaveResponse.errorMessage()));
        } else if (response == null) {
            return new StoredLargeDataSaveResult(false, new StoredError(4, SQLSTATE_CONNECTIONERROR, "No response"));
        } else {
            return new StoredLargeDataSaveResult(false, new StoredError(5, SQLSTATE_CONNECTIONERROR, "Bad response"));
        }
    }
    
    private int readStream(InputStream in, byte[] buffer) {
        try {
            return in.read(buffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    public void close() {
        closed = true;
        long sessionId = sessionId();
        int exchangeId = exchangeIdCounter.incrementAndGet();
        Request sessionCloseRequest = new SessionCloseRequest(sessionId, exchangeId);
        CompletableFuture<SessionCloseResponse> closeFuture = new CompletableFuture<>();
        messenger.accept(sessionCloseRequest, r -> acceptSessionCloseResponse(r, closeFuture));
        waitForFutureSilently(closeFuture);
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

    private void acceptSessionCloseResponse(
            Response response, CompletableFuture<SessionCloseResponse> future) {
        if (!(response instanceof SessionCloseResponse)) {
            return;
        }
        SessionCloseResponse sessionCloseResponse = (SessionCloseResponse) response;
        future.complete(sessionCloseResponse);
    }
    
    private static void waitForFutureSilently(Future<?> future) {
        try {
            future.get(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // nothing other to do
        } catch (Exception e) {
            // nothing to do
        }
    }

}
