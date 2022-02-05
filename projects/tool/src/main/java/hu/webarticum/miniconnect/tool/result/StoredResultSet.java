package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public final class StoredResultSet implements MiniResultSet, Serializable {

    private static final long serialVersionUID = 1L;


    private final StoredResultSetData data;
    
    private transient Iterator<ImmutableList<MiniValue>> iterator;


    public StoredResultSet() {
        this(new StoredResultSetData());
    }

    public StoredResultSet(StoredResultSetData data) {
        this.data = data;
        this.iterator = data.iterator();
    }
    
    private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        this.iterator = data.iterator();
    }


    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return data.columnHeaders();
    }

    @Override
    public Iterator<ImmutableList<MiniValue>> iterator() {
        return iterator;
    }
    
    public ImmutableList<ImmutableList<MiniValue>> rows() {
        return data.rows();
    }

    @Override
    public void close() {
        // nothing to do
    }

}
