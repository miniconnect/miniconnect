package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.data.ByteString;

public final class LargeDataPartRequest implements Request, ExchangeMessage {

    private final long sessionId;

    private final int exchangeId;

    private final long offset;

    private final ByteString content;


    public LargeDataPartRequest(long sessionId, int exchangeId, long offset, ByteString content) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.offset = offset;
        this.content = content;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    public long offset() {
        return offset;
    }

    public ByteString content() {
        return content;
    }

}
