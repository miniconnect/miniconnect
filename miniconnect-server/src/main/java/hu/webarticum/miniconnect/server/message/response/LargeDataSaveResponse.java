package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public class LargeDataSaveResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final String variableName;


    public LargeDataSaveResponse(
            long sessionId,
            int exchangeId,
            boolean success,
            String errorCode,
            String errorMessage,
            String variableName) {

        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.variableName = variableName;
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

    public String errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public String getVariableName() {
        return variableName;
    }

}
