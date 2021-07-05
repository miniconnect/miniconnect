package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public class LargeDataSaveResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final boolean success;
    
    private final String sqlState;

    private final String errorCode;

    private final String errorMessage;


    public LargeDataSaveResponse(
            long sessionId,
            int exchangeId,
            boolean success,
            String sqlState,
            String errorCode,
            String errorMessage) {

        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.success = success;
        this.sqlState = sqlState;
        this.errorCode = errorCode;
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

    public String sqlState() {
        return sqlState;
    }
    
    public String errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

}
