package hu.webarticum.miniconnect.api;

public class MiniErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    

    private final int code;

    private final String sqlState;

    
    public MiniErrorException(MiniError error) {
        super(error.message());
        this.code = error.code();
        this.sqlState = error.sqlState();
    }
    

    public int code() {
        return code;
    }

    public String sqlState() {
        return sqlState;
    }

}
