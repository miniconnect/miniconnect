package hu.webarticum.miniconnect.api;

public interface MiniError {

    public int code();

    public String sqlState();

    public String message();

}
