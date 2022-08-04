package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public final class StoredLargeDataSaveResult implements MiniLargeDataSaveResult, Serializable {

    private static final long serialVersionUID = 1L;
    
    
    private final boolean success;
    
    private final StoredError error;
    
    
    public StoredLargeDataSaveResult() {
        this(true, null);
    }

    public StoredLargeDataSaveResult(MiniError error) {
        this(false, StoredError.of(error));
    }
    
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
