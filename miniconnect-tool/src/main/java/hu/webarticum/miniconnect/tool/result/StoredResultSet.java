package hu.webarticum.miniconnect.tool.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class StoredResultSet implements MiniResultSet, Serializable {

    private static final long serialVersionUID = 1L;


    private final StoredResultSetData data;

    private int position = 0;


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
    public ImmutableList<MiniValue> fetch() {
        ImmutableList<ImmutableList<MiniValue>> rows = rows();
        if (position >= rows.size()) {
            return null;
        }

        ImmutableList<MiniValue> result = rows.get(position);
        position++;

        return result;
    }

    public ImmutableList<ImmutableList<MiniValue>> rows() {
        return data.rows();
    }

    @Override
    public void close() {
        // nothing to do
    }

}
