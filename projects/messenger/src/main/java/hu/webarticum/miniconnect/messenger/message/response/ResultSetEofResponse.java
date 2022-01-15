package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class ResultSetEofResponse implements Response, ExchangeMessage {

    private final long sessionId;

    private final int exchangeId;

    private final long endOffset;

    
    public ResultSetEofResponse(long sessionId, int exchangeId, long endOffset) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.endOffset = endOffset;
    }

    
    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    public long endOffset() {
        return endOffset;
    }

}
