package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public final class StoredResultSet implements MiniResultSet, Serializable {

    private static final long serialVersionUID = 1L;


    private final StoredResultSetData data;

    private int position = 0;


    private StoredResultSet(StoredResultSetData data) {
        this.data = data;
    }

    public static StoredResultSet of(StoredResultSetData data) {
        return new StoredResultSet(data);
    }

    public static StoredResultSet empty() {
        return of(StoredResultSetData.empty());
    }


    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return data.columnHeaders();
    }

    @Override
    public ImmutableList<MiniValue> fetch() {
        ImmutableList<ImmutableList<MiniValue>> rows = rows();
        if (position >= rows.size()) {
            return null;
        }

        ImmutableList<MiniValue> result = rows.get(position);
        position++;

        return result;
    }

    public StoredResultSetData data() {
        return data;
    }

    public ImmutableList<ImmutableList<MiniValue>> rows() {
        return data.rows();
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public boolean isClosed() {
        return false;
    }

}
