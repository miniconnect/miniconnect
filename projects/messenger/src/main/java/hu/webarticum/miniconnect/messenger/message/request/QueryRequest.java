package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public final class QueryRequest implements Request, SessionMessage {

    private final long sessionId;

    // FIXME: int? (long? String? byte[]?)
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
