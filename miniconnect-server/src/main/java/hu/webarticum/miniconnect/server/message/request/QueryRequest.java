package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public final class QueryRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int id;

    private final String query;

    private final long maxRowCount;


    public QueryRequest(long sessionId, int id, String query, long maxRowCount) {
        this.sessionId = sessionId;
        this.id = id;
        this.query = query;
        this.maxRowCount = maxRowCount;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int id() {
        return id;
    }

    public String query() {
        return query;
    }

    public long maxRowCount() {
        return maxRowCount;
    }

}
