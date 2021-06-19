package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public final class LargeDataHeadRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final long length;


    public LargeDataHeadRequest(long sessionId, int exchangeId, long length) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.length = length;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    public long length() {
        return length;
    }

}
