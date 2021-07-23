package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public final class LargeDataSaveResponse implements Response, SessionMessage {

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
        this.sqlState = sqlState;
        this.errorMessage = errorMessage;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

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

}
