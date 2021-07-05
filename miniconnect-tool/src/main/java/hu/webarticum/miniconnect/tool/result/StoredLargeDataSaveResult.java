package hu.webarticum.miniconnect.tool.result;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public class StoredLargeDataSaveResult implements MiniLargeDataSaveResult {
    
    private final boolean success;
    
    private final String sqlState;
    
    private final String errorCode;
    
    private final String errorMessage;
    

    public StoredLargeDataSaveResult(
            boolean success,
            String sqlState,
            String errorCode,
            String errorMessage) {

        this.success = success;
        this.sqlState = sqlState;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String sqlState() {
        return sqlState;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

}
