package hu.webarticum.miniconnect.tool.result;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class StoredResultSetData implements Serializable {

    private static final long serialVersionUID = 1L;


    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<ImmutableList<MiniValue>> rows;


    public StoredResultSetData() {
        this(new ImmutableList<>(), new ImmutableList<>());
    }

    public StoredResultSetData(
            ImmutableList<? extends MiniColumnHeader> columnHeaders,
            ImmutableList<ImmutableList<? extends MiniValue>> rows) {

        this.columnHeaders = new ImmutableList<>(columnHeaders.stream()
                .map(StoredColumnHeader::of)
                .collect(Collectors.toList()));
        this.rows = new ImmutableList<>(rows.stream()
                .map(row -> new ImmutableList<>(row.stream()
                        .map(value -> (MiniValue) StoredValue.of(value))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    public StoredResultSetData(
            List<? extends MiniColumnHeader> columnHeaders,
            List<? extends List<? extends MiniValue>> rows) {

        this.columnHeaders = new ImmutableList<>(columnHeaders.stream()
                .map(StoredColumnHeader::of)
                .collect(Collectors.toList()));
        this.rows = new ImmutableList<>(rows.stream()
                .map(row -> new ImmutableList<>(row.stream()
                        .map(value -> (MiniValue) StoredValue.of(value))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }


    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    public Iterator<ImmutableList<MiniValue>> iterator() {
        return rows.iterator();
    }

}
