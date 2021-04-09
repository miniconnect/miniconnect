package hu.webarticum.miniconnect.server.message.response;

public class LobResultResponse {

    private final int lobId;

    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final String variableName;


    public LobResultResponse(
            int lobId,
            boolean success,
            String errorCode,
            String errorMessage,
            String variableName) {

        this.lobId = lobId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.variableName = variableName;
    }


    public int getLobId() {
        return lobId;
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
