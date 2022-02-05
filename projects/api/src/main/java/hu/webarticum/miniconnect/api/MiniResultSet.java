package hu.webarticum.miniconnect.api;

import java.io.Closeable;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface MiniResultSet extends Closeable, Iterable<ImmutableList<MiniValue>> {

    public ImmutableList<MiniColumnHeader> columnHeaders();
    
    @Override
    public void close();

}
