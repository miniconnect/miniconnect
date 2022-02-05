package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;

public final class StoredError implements MiniError, Serializable {

    private static final long serialVersionUID = 1L;
    
    
    public static final StoredError PLACEHOLDER = new StoredError(0, "00000", "");
    
    
    private final int code;
    
    private final String sqlState;
    
    private final String message;
    
    
    public StoredError(int code, String sqlState, String message) {
        this.code = code;
        this.sqlState = sqlState;
        this.message = message;
    }
    
    public static StoredError of(MiniError error) {
        if (error instanceof StoredError) {
            return (StoredError) error;
        }
        
        return new StoredError(error.code(), error.sqlState(), error.message());
    }
    

    @Override
    public int code() {
        return code;
    }

    @Override
    public String sqlState() {
        return sqlState;
    }

    @Override
    public String message() {
        return message;
    }

}
