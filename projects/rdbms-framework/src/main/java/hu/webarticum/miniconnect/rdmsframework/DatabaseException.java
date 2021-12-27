package hu.webarticum.miniconnect.rdmsframework;

public class DatabaseException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    

    private final int code;
    
    private final String sqlState;
    

    public DatabaseException(int code, String sqlState, String message) {
        this(code, sqlState, message, null);
    }

    public DatabaseException(int code, String sqlState, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.sqlState = sqlState;
    }
    

    public int code() {
        return code;
    }

    public String sqlState() {
        return sqlState;
    }

    public String message() {
        return getMessage();
    }
    
}
