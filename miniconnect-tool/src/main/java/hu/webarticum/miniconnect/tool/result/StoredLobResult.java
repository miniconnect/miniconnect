package hu.webarticum.miniconnect.tool.result;

import hu.webarticum.miniconnect.api.MiniLobResult;

public class StoredLobResult implements MiniLobResult {
    
    private final boolean success;
    
    private final String errorCode;
    
    private final String errorMessage;
    
    private final String variableName;
    

    public StoredLobResult(
            boolean success,
            String errorCode,
            String errorMessage,
            String variableName) {

        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.variableName = variableName;
    }
    
    public static StoredLobResult success(String variableName) {
        return new StoredLobResult(true, "", "", variableName);
    }

    public static StoredLobResult error(String errorCode, String errorMessage) {
        return new StoredLobResult(false, errorCode, errorMessage, "");
    }
    

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public String variableName() {
        return variableName;
    }

}
