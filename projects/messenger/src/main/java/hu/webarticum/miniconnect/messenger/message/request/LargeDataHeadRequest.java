package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class LargeDataHeadRequest implements Request, ExchangeMessage {

    private final long sessionId;

    // FIXME: int? (long? String? byte[]?)
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

    @Override
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
