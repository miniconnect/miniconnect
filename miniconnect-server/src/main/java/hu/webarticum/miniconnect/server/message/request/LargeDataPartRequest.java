package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;

public final class LargeDataPartRequest implements Request, SessionMessage {

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
