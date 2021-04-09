package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.util.data.ImmutableList;

// TODO: affected row count, last inserted id etc.
public interface MiniResult {

    public boolean success();

    public String errorCode();

    public String errorMessage();

    public ImmutableList<String> warnings();

    public boolean hasResultSet();

    // FIXME: subsequent calls? throws?
    public MiniResultSet resultSet();

}
