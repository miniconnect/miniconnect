package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public final class ResultSetFetchRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int queryId;

    private final long maxRowCount;


    public ResultSetFetchRequest(long sessionId, int queryId, long maxRowCount) {
        this.sessionId = sessionId;
        this.queryId = queryId;
        this.maxRowCount = maxRowCount;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
    }

    public long maxRowCount() {
        return maxRowCount;
    }

}
