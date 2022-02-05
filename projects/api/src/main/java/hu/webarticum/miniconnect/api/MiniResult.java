package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface MiniResult {

    public boolean success();

    public MiniError error();

    public ImmutableList<MiniError> warnings();

    public boolean hasResultSet();

    public MiniResultSet resultSet();

}
