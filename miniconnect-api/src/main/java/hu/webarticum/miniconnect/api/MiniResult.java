package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface MiniResult {

    public boolean success();

    public String errorCode();

    public String errorMessage();

    public ImmutableList<String> warnings();

    public boolean hasResultSet();

    public MiniResultSet resultSet();

}
