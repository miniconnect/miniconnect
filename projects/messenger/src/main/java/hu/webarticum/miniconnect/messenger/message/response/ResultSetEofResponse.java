package hu.webarticum.miniconnect.messenger.message.response;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class ResultSetEofResponse implements Response, ExchangeMessage {

    private static final long serialVersionUID = -4891187334638415850L;
    

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

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, endOffset);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ResultSetEofResponse)) {
            return false;
        }
        
        ResultSetEofResponse otherResultSetEofResponse = (ResultSetEofResponse) other;
        return
                sessionId == otherResultSetEofResponse.sessionId &&
                exchangeId == otherResultSetEofResponse.exchangeId &&
                endOffset == otherResultSetEofResponse.endOffset;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("endOffset", endOffset)
                .build();
    }

}
