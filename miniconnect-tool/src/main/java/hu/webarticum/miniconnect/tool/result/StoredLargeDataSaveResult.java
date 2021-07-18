package hu.webarticum.miniconnect.tool.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public class StoredLargeDataSaveResult implements MiniLargeDataSaveResult, Serializable {

    private static final long serialVersionUID = 1L;
    
    
    private final boolean success;
    
    private final StoredError error;
    

    public StoredLargeDataSaveResult(boolean success, StoredError error) {
        this.success = success;
        this.error = error;
    }
    

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public MiniError error() {
        return error;
    }

}
