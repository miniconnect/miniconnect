package hu.webarticum.miniconnect.messenger.message.response;

import java.util.Objects;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.ToStringBuilder;

public final class LargeDataSaveResponse implements Response, ExchangeMessage {

    private final long sessionId;

    private final int exchangeId;

    private final boolean success;

    private final int errorCode;

    private final String sqlState;

    private final String errorMessage;


    public LargeDataSaveResponse(
            long sessionId,
            int exchangeId,
            boolean success,
            int errorCode,
            String sqlState,
            String errorMessage) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.success = success;
        this.errorCode = errorCode;
        this.sqlState = Objects.requireNonNull(sqlState);
        this.errorMessage = Objects.requireNonNull(errorMessage);
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    public boolean success() {
        return success;
    }

    public int errorCode() {
        return errorCode;
    }

    public String sqlState() {
        return sqlState;
    }
    
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, success, errorCode, sqlState, errorMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof LargeDataSaveResponse)) {
            return false;
        }
        
        LargeDataSaveResponse otherLargeDataSaveResponse = (LargeDataSaveResponse) other;
        return
                sessionId == otherLargeDataSaveResponse.sessionId &&
                exchangeId == otherLargeDataSaveResponse.exchangeId &&
                success == otherLargeDataSaveResponse.success &&
                errorCode == otherLargeDataSaveResponse.errorCode &&
                sqlState.equals(otherLargeDataSaveResponse.sqlState) &&
                errorMessage.equals(otherLargeDataSaveResponse.errorMessage);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("success", success)
                .add("errorCode", errorCode)
                .add("sqlState", sqlState)
                .add("errorMessage", errorMessage)
                .build();
    }

}
