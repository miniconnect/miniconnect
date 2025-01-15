package hu.webarticum.miniconnect.messenger.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.request.SessionCloseRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;

public class SessionManagerMessenger implements Messenger {

    private static final int MAX_THREAD_COUNT = 64;
    
    
    private final MiniSessionManager sessionManager;
    
    private final AtomicLong sessionIdCounter = new AtomicLong(0L);

    private final Map<Long, SessionMessenger> sessionMessengers =
            Collections.synchronizedMap(new HashMap<>());
    
    private final ExecutorService sessionInitExecutorService =
            new ThreadPoolExecutor(0, MAX_THREAD_COUNT, 1L, TimeUnit.SECONDS, new SynchronousQueue<>());
    
    
    public SessionManagerMessenger(MiniSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    
    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof SessionInitRequest) {
            sessionInitExecutorService.submit(() -> invokeOpenSession(responseConsumer));
        } else if (request instanceof SessionMessage) {
            long sessionId = ((SessionMessage) request).sessionId();
            SessionMessenger sessionMessenger = sessionMessengers.get(sessionId);
            if (sessionMessenger != null) {
                if (request instanceof SessionCloseRequest) {
                    // FIXME: what if no response comes? force remove on timeout?
                    sessionMessenger.accept(request, r -> {
                        responseConsumer.accept(r);
                        sessionMessengers.remove(sessionId);
                    });
                } else {
                    sessionMessenger.accept(request, responseConsumer);
                }
            } else {
                // FIXME: log?
            }
        } else {
            // FIXME: log?
        }
    }
    
    private void invokeOpenSession(Consumer<Response> responseConsumer) {
        long sessionId = sessionIdCounter.incrementAndGet();
        MiniSession session = sessionManager.openSession();
        SessionMessenger sessionMessenger = new SessionMessenger(sessionId, session);
        sessionMessengers.put(sessionId, sessionMessenger);
        responseConsumer.accept(new SessionInitResponse(sessionId));
    }

}
