package hu.webarticum.miniconnect.tool.result;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public class StoredLargeDataSaveResult implements MiniLargeDataSaveResult {
    
    private final boolean success;
    
    private final String errorCode;
    
    private final String errorMessage;
    
    private final String variableName;
    

    public StoredLargeDataSaveResult(
            boolean success,
            String errorCode,
            String errorMessage,
            String variableName) {

        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.variableName = variableName;
    }
    
    public static StoredLargeDataSaveResult success(String variableName) {
        return new StoredLargeDataSaveResult(true, "", "", variableName);
    }

    public static StoredLargeDataSaveResult error(String errorCode, String errorMessage) {
        return new StoredLargeDataSaveResult(false, errorCode, errorMessage, "");
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
