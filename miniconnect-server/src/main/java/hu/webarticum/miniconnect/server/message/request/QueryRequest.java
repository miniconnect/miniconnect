package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public final class QueryRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final String query;


    public QueryRequest(long sessionId, int exchangeId, String query) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.query = query;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    public String query() {
        return query;
    }

}
