package hu.webarticum.miniconnect.api;

public interface MiniLargeDataSaveResult {

    public boolean success();

    public String sqlState();

    public String errorCode();

    public String errorMessage();

}
