package hu.webarticum.miniconnect.messenger.impl;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.response.Response;

class QueryPartial {

    private final long sessionId;
    
    private final MiniSession session;

    
    public QueryPartial(long sessionId, MiniSession session) {
        this.sessionId = sessionId;
        this.session = session;
    }
    
    
    public void acceptQueryRequest(QueryRequest request, Consumer<Response> responseConsumer) {

        // TODO
        
    }

}
