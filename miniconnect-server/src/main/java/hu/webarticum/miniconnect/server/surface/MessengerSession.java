package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.server.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.server.util.OrderAligningQueue;
import hu.webarticum.miniconnect.tool.result.StoredLargeDataSaveResult;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.server.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.server.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerSession implements MiniSession {
    
    private static final int DATA_SEND_CHUNK_SIZE = 4096; // TODO: make it configurable
    
    private static final int RESULT_TIMEOUT_VALUE = 60; // TODO: make it configurable
    
    private static final TimeUnit RESULT_TIMEOUT_UNIT = TimeUnit.SECONDS; // TODO: make it conf.
    
    
    private final long sessionId;
    
    private final Server server;
    

    private final AtomicInteger exchangeIdCounter = new AtomicInteger();


    public MessengerSession(long sessionId, Server server) {
        this.sessionId = sessionId;
        this.server = server;
    }

    @Override
    public MiniResult execute(String query) throws IOException {
        int exchangeId = exchangeIdCounter.incrementAndGet();
        
        OrderAligningQueue<Response> responseQueue = new OrderAligningQueue<>(
                MessengerSession::checkNextResultResponse);

        CompletableFuture<MessengerResultSet> resultSetFuture = new CompletableFuture<>();
        
        QueryRequest queryRequest = new QueryRequest(sessionId, exchangeId, query);
        server.accept(queryRequest, response -> {
            if (response instanceof ResultSetValuePartResponse) {
                ResultSetValuePartResponse partResponse = (ResultSetValuePartResponse) response;
                resultSetFuture.thenAcceptAsync(resultSet -> resultSet.accept(partResponse));
            } else {
                responseQueue.add(response);
            }
        });

        Response firstResponse;
        try {
            firstResponse = responseQueue.take(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            resultSetFuture.cancel(true);
            return new StoredResult("INTERRUPT", "Interrupt occured while waiting for results");
        } catch (TimeoutException e) {
            resultSetFuture.cancel(true);
            return new StoredResult("TIMEOUT", "Timeout reached while waiting for results");
        }
        
        if (!(firstResponse instanceof ResultResponse)) {
            resultSetFuture.cancel(true);
            return new StoredResult("BADRESPONSE", "Bad response");
        }

        ResultResponse resultResponse = (ResultResponse) firstResponse;
        if (!resultResponse.success()) {
            resultSetFuture.cancel(true);
            return new StoredResult(resultResponse.errorCode(), resultResponse.errorMessage());
        }
        
        MessengerResultSet resultSet = new MessengerResultSet(resultResponse);
        resultSetFuture.complete(resultSet);
        new Thread(() -> pollResponseQueue(responseQueue, resultSet)).start();
        
        return new MessengerResult(resultResponse, resultSet);
    }
    
    // TODO: error handling
    private void pollResponseQueue(
            OrderAligningQueue<Response> responseQueue, MessengerResultSet resultSet) {
        
        while (fetchResponseQueue(responseQueue, resultSet)) {
            // nothing to do
        }
    }
        
    private boolean fetchResponseQueue(
            OrderAligningQueue<Response> responseQueue, MessengerResultSet resultSet) {
        
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
            resultSet.eof();
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
    public MiniLargeDataSaveResult putLargeData(
            long length, InputStream dataSource) throws IOException {
        
        int exchangeId = exchangeIdCounter.incrementAndGet();
        
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        
        LargeDataHeadRequest largeDataHeadRequest =
                new LargeDataHeadRequest(sessionId, exchangeId, length);
        server.accept(largeDataHeadRequest, responseFuture::complete);

        byte[] buffer = new byte[DATA_SEND_CHUNK_SIZE];
        int readSize = 0;
        long offset = 0;
        while ((readSize = dataSource.read(buffer)) != -1) {
            // TODO: check for error
            ByteString content = ByteString.wrap(Arrays.copyOf(buffer, readSize));
            LargeDataPartRequest largeDataPartRequest =
                    new LargeDataPartRequest(sessionId, exchangeId, offset, content);
            server.accept(largeDataPartRequest);
            offset += readSize;
        }
        
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
                    largeDataSaveResponse.errorCode(),
                    largeDataSaveResponse.errorMessage(),
                    largeDataSaveResponse.getVariableName());
        } else if (response == null) {
            return new StoredLargeDataSaveResult(false, "99990", "No response", ""); // XXX
        } else {
            return new StoredLargeDataSaveResult(false, "99999", "Bad response", ""); // XXX
        }
    }
    
    @Override
    public void close() throws IOException {
        
        // TODO

    }

}
