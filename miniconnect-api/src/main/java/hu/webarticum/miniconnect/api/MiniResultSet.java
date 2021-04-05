package hu.webarticum.miniconnect.api;

import java.io.Closeable;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface MiniResultSet extends Closeable, Iterable<ImmutableList<MiniValue>> {

    public ImmutableList<MiniColumnHeader> columnHeaders();

    public boolean isClosed();

}
