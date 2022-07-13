package hu.webarticum.miniconnect.api;

import java.io.Closeable;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface MiniResultSet extends Closeable {

    public ImmutableList<MiniColumnHeader> columnHeaders();
    
    public ImmutableList<MiniValue> fetch();
    
    @Override
    public void close();

}
