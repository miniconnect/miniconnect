package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public final class StoredResultSetData implements Iterable<ImmutableList<MiniValue>>, Serializable {

    private static final long serialVersionUID = 1L;


    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<ImmutableList<MiniValue>> rows;


    public StoredResultSetData() {
        this(ImmutableList.empty(), ImmutableList.empty());
    }

    public StoredResultSetData(
            List<? extends MiniColumnHeader> columnHeaders,
            List<? extends List<? extends MiniValue>> rows) {
        this.columnHeaders = columnHeaders.stream()
                .map(StoredColumnHeader::of)
                .collect(ImmutableList.createCollector());
        this.rows = rows.stream()
                .map(row -> row.stream()
                        .map(value -> (MiniValue) StoredValue.of(value))
                        .collect(ImmutableList.createCollector()))
                .collect(ImmutableList.createCollector());
    }

    public StoredResultSetData(
            ImmutableList<? extends MiniColumnHeader> columnHeaders,
            ImmutableList<? extends ImmutableList<? extends MiniValue>> rows) {
        this.columnHeaders = columnHeaders.map(StoredColumnHeader::of);
        this.rows = rows.map(row -> row.map(
                value -> (MiniValue) StoredValue.of(value)));
    }

    public static StoredResultSetData of(MiniResult result) {
        MiniResultSet resultSet = result.resultSet();
        List<MiniColumnHeader> headers = resultSet.columnHeaders().asList();
        List<List<MiniValue>> rows = new ArrayList<>();
        ImmutableList<MiniValue> row;
        while ((row = resultSet.fetch()) != null) {
            rows.add(row.asList());
        }
        return new StoredResultSetData(headers, rows);
    }


    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    @Override
    public Iterator<ImmutableList<MiniValue>> iterator() {
        return rows.iterator();
    }

    public ImmutableList<ImmutableList<MiniValue>> rows() {
        return rows;
    }

}
