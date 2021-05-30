package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public class ResultSetEofResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int queryId;

    private final long endOffset;

    
    public ResultSetEofResponse(long sessionId, int queryId, long endOffset) {
        this.sessionId = sessionId;
        this.queryId = queryId;
        this.endOffset = endOffset;
    }

    
    @Override
    public long sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
    }

    public long endOffset() {
        return endOffset;
    }

}
