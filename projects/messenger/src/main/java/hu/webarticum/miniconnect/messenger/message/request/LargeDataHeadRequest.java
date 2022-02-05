package hu.webarticum.miniconnect.messenger.message.request;

import java.util.Objects;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.ToStringBuilder;

public final class LargeDataHeadRequest implements Request, ExchangeMessage {

    private final long sessionId;

    private final int exchangeId;

    private final String variableName;

    private final long length;


    public LargeDataHeadRequest(long sessionId, int exchangeId, String variableName, long length) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.variableName = Objects.requireNonNull(variableName);
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

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, variableName, length);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof LargeDataHeadRequest)) {
            return false;
        }
        
        LargeDataHeadRequest otherLargeDataHeadRequest = (LargeDataHeadRequest) other;
        return
                sessionId == otherLargeDataHeadRequest.sessionId &&
                exchangeId == otherLargeDataHeadRequest.exchangeId &&
                variableName.equals(otherLargeDataHeadRequest.variableName) &&
                length == otherLargeDataHeadRequest.length;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("variableName", variableName)
                .add("length", length)
                .build();
    }

}
