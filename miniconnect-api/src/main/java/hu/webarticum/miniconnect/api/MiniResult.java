package hu.webarticum.miniconnect.api;

import java.util.List;

public interface MiniResult {

    public boolean success();

    public String errorCode();

    public String errorMessage();

    public List<String> warnings();

    public boolean hasResultSet();

    public MiniResultSet resultSet();

}
