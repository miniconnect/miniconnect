package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class StoredResultSet implements MiniResultSet, Serializable {

    private static final long serialVersionUID = 1L;


    private final StoredResultSetData data;


    private volatile boolean closed = false;


    public StoredResultSet() {
        this(new StoredResultSetData());
    }

    public StoredResultSet(StoredResultSetData data) {
        this.data = data;
    }


    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return data.columnHeaders();
    }

    @Override
    public Iterator<ImmutableList<MiniValue>> iterator() {
        return data.iterator();
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
