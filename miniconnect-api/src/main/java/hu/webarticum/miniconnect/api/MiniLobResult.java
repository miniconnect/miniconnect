package hu.webarticum.miniconnect.api;

public interface MiniLobResult {

    public boolean success();

    public String errorCode();

    public String errorMessage();

    public String variableName();

}
