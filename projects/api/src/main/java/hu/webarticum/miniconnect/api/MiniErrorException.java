package hu.webarticum.miniconnect.api;

public class MiniErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    

    private final int code;

    private final String sqlState;


    public MiniErrorException(MiniError error) {
        this(error.code(), error.sqlState(), error.message());
    }
    
    public MiniErrorException(int code, String sqlState, String message) {
        super(message);
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
