package hu.webarticum.miniconnect.messenger.message.response;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class SessionCloseResponse implements Response, ExchangeMessage {

    private static final long serialVersionUID = 2580416468973712490L;
    

    private final long sessionId;

    private final int exchangeId;


    public SessionCloseResponse(long sessionId, int exchangeId) {
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
        } else if (!(other instanceof SessionCloseResponse)) {
            return false;
        }
        
        SessionCloseResponse otherLargeDataSaveResponse = (SessionCloseResponse) other;
        return
                sessionId == otherLargeDataSaveResponse.sessionId &&
                exchangeId == otherLargeDataSaveResponse.exchangeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .build();
    }

}
