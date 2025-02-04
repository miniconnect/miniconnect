package hu.webarticum.miniconnect.messenger.message.request;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class SessionCloseRequest implements Request, ExchangeMessage {

    private static final long serialVersionUID = -7193819981812764503L;
    

    private final long sessionId;

    private final int exchangeId;


    public SessionCloseRequest(long sessionId, int exchangeId) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof SessionCloseRequest)) {
            return false;
        }
        
        SessionCloseRequest otherSessionCloseRequest = (SessionCloseRequest) other;
        return
                sessionId == otherSessionCloseRequest.sessionId &&
                exchangeId == otherSessionCloseRequest.exchangeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .build();
    }

}
