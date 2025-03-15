package hu.webarticum.miniconnect.messenger.message.request;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class LargeDataPartRequest implements Request, ExchangeMessage {

    private static final long serialVersionUID = 4207615629289430723L;
    

    private final long sessionId;

    private final int exchangeId;

    private final long offset;

    private final ByteString content;


    public LargeDataPartRequest(long sessionId, int exchangeId, long offset, ByteString content) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.offset = offset;
        this.content = Objects.requireNonNull(content);
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

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, offset, content);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof LargeDataPartRequest)) {
            return false;
        }
        
        LargeDataPartRequest otherLargeDataPartRequest = (LargeDataPartRequest) other;
        return
                sessionId == otherLargeDataPartRequest.sessionId &&
                exchangeId == otherLargeDataPartRequest.exchangeId &&
                offset == otherLargeDataPartRequest.offset &&
                content.equals(otherLargeDataPartRequest.content);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("offset", offset)
                .add("content", content.toArrayString())
                .build();
    }

}
