package hu.webarticum.miniconnect.messenger.impl;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;

// FIXME: should stranger session ids be filtered?
public class SessionMessenger implements Messenger {
    
    private final QueryPartial queryPartial;
    
    private final LargeDataPartial largeDataPartial;
    
    
    public SessionMessenger(long sessionId, MiniSession session) {
        this.queryPartial = new QueryPartial(sessionId, session);
        this.largeDataPartial = new LargeDataPartial(sessionId, session);
    }
    
    
    @Override
    public synchronized void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof QueryRequest) {
            queryPartial.acceptQueryRequest((QueryRequest) request, responseConsumer);
        } else if (request instanceof LargeDataHeadRequest) {
            largeDataPartial.acceptLargeDataHeadRequest(
                    (LargeDataHeadRequest) request, responseConsumer);
        } else if (request instanceof LargeDataPartRequest) {
            largeDataPartial.acceptLargeDataPartRequest((LargeDataPartRequest) request);
        } else {
            throw new UnsupportedOperationException(String.format(
                    "Unsupported request type: %s",
                    request.getClass().getSimpleName()));
        }
    }

}
