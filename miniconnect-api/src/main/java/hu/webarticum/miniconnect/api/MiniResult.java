package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.util.data.ImmutableList;

// FIXME: error/warning holder? exception?
public interface MiniResult {

    public boolean success();

    public String errorCode();

    public String sqlState();

    public String errorMessage();

    public ImmutableList<String> warnings();

    public boolean hasResultSet();

    public MiniResultSet resultSet();

}
