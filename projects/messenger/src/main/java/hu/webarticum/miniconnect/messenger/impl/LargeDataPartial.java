package hu.webarticum.miniconnect.messenger.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.util.OrderAligningQueue;

class LargeDataPartial implements Closeable {

    private static final String SQLSTATE_CONNECTIONERROR = "08006";
    
    
    private final long sessionId;
    
    private final MiniSession session;
    
    private final ExecutorService putLargeDataInvokerExecutorService =
            Executors.newCachedThreadPool();
    
    private final ExecutorService largeDataPartExecutorService =
            Executors.newCachedThreadPool();
    
    private final Map<Integer, OrderAligningQueue<LargeDataPartRequest>> largeDataPartRequests =
            new HashMap<>();
    

    public LargeDataPartial(long sessionId, MiniSession session) {
        this.sessionId = sessionId;
        this.session = session;
    }
    
    
    public void acceptLargeDataHeadRequest(
            LargeDataHeadRequest headRequest, Consumer<Response> responseConsumer) {
        int exchangeId = headRequest.exchangeId();
        OrderAligningQueue<LargeDataPartRequest> partQueue =
                requireLargeDataPartQueue(exchangeId);
        putLargeDataInvokerExecutorService.submit(
                () -> invokePutLargeData(headRequest, responseConsumer, partQueue));
    }

    public void acceptLargeDataPartRequest(LargeDataPartRequest partRequest) {
        int exchangeId = partRequest.exchangeId();
        OrderAligningQueue<LargeDataPartRequest> partQueue =
                requireLargeDataPartQueue(exchangeId);
        partQueue.add(partRequest);
    }
    
    private OrderAligningQueue<LargeDataPartRequest> requireLargeDataPartQueue(int exchangeId) {
        return largeDataPartRequests.computeIfAbsent(
                exchangeId,
                k -> new OrderAligningQueue<LargeDataPartRequest>(this::isNextPart));
    }

    private boolean isNextPart(LargeDataPartRequest previous, LargeDataPartRequest next) {
        if (previous == null) {
            return (next.offset() == 0L);
        }
        
        long leftOffset = previous.offset() + previous.content().length();
        return (next.offset() == leftOffset);
    }

    private void invokePutLargeData(
            LargeDataHeadRequest headRequest,
            Consumer<Response> responseConsumer,
            OrderAligningQueue<LargeDataPartRequest> partQueue) {
        
        int exchangeId = headRequest.exchangeId();

        try {
            invokePutLargeDataThrowing(headRequest, responseConsumer, partQueue);
        } catch (Exception e) {
            LargeDataSaveResponse response = new LargeDataSaveResponse(
                    sessionId,
                    exchangeId,
                    false,
                    999, // XXX
                    SQLSTATE_CONNECTIONERROR,
                    e.getMessage());
            responseConsumer.accept(response);
        }
        
        synchronized (this) {
            largeDataPartRequests.remove(exchangeId);
        }
    }
    
    private void invokePutLargeDataThrowing(
            LargeDataHeadRequest headRequest,
            Consumer<Response> responseConsumer,
            OrderAligningQueue<LargeDataPartRequest> partQueue
            ) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        
        int exchangeId = headRequest.exchangeId();
        String variableName = headRequest.variableName();
        long fullLength = headRequest.length();

        MiniLargeDataSaveResult result;
        PipedInputStream in = new PipedInputStream();
        try (PipedOutputStream out = new PipedOutputStream(in)) {
            Future<MiniLargeDataSaveResult> resultFuture = largeDataPartExecutorService.submit(
                    () -> session.putLargeData(variableName, fullLength, in));
            
            long writtenOffset = 0L;
            while (writtenOffset < fullLength) {
                // FIXME: timeout???
                LargeDataPartRequest partRequest = partQueue.take(300, TimeUnit.SECONDS);
                ByteString content = partRequest.content();
                content.writeTo(out);
                writtenOffset += content.length();
            }
            
            // FIXME: timeout???
            result = resultFuture.get(300, TimeUnit.SECONDS);
        }
        
        MiniError error = result.error();
        LargeDataSaveResponse response = new LargeDataSaveResponse(
                sessionId,
                exchangeId,
                result.success(),
                error.code(),
                error.sqlState(),
                error.message());
        responseConsumer.accept(response);
    }
    
    @Override
    public void close() {
        putLargeDataInvokerExecutorService.shutdownNow();
        largeDataPartExecutorService.shutdownNow();
    }

}
