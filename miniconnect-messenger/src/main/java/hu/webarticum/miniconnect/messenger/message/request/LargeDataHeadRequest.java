package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public final class LargeDataHeadRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final String variableName;

    private final long length;


    public LargeDataHeadRequest(long sessionId, int exchangeId, String variableName, long length) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.variableName = variableName;
        this.length = length;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    public String variableName() {
        return variableName;
    }

    public long length() {
        return length;
    }

}
