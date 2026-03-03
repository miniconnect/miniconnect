package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public final class StoredResultSetData implements Iterable<ImmutableList<MiniValue>>, Serializable {

    private static final long serialVersionUID = 1L;


    private final ImmutableList<StoredColumnHeader> columnHeaders;

    private final ImmutableList<ImmutableList<StoredValue>> rows;


    private StoredResultSetData(ImmutableList<StoredColumnHeader> columnHeaders, ImmutableList<ImmutableList<StoredValue>> rows) {
        this.columnHeaders = columnHeaders;
        this.rows = rows;
    }

    public static StoredResultSetData of(
            ImmutableList<StoredColumnHeader> columnHeaders,
            ImmutableList<ImmutableList<StoredValue>> rows) {
        return new StoredResultSetData(columnHeaders, rows);
    }

    public static StoredResultSetData empty() {
        return of(ImmutableList.empty(), ImmutableList.empty());
    }

    public static StoredResultSetData from(
            List<? extends MiniColumnHeader> columnHeaders,
            List<? extends List<? extends MiniValue>> rows) {
        return of(
                columnHeaders.stream()
                        .map(header -> StoredColumnHeader.from(header))
                        .collect(ImmutableList.createCollector()),
                rows.stream()
                        .map(row -> row.stream()
                                .map(value -> StoredValue.from(value))
                                .collect(ImmutableList.createCollector()))
                        .collect(ImmutableList.createCollector()));
    }

    public static StoredResultSetData from(
            ImmutableList<? extends MiniColumnHeader> columnHeaders,
            ImmutableList<? extends ImmutableList<? extends MiniValue>> rows) {
        return of(
                columnHeaders.map(StoredColumnHeader::from),
                rows.map(row -> row.map(value -> StoredValue.from(value))));
    }

    public static StoredResultSetData from(MiniResultSet resultSet) {
        if (resultSet instanceof StoredResultSet) {
            return ((StoredResultSet) resultSet).data();
        }

        List<ImmutableList<StoredValue>> storedRows = new ArrayList<>();
        ImmutableList<MiniValue> row;
        while ((row = resultSet.fetch()) != null) {
            storedRows.add(row.map(value -> StoredValue.from(value)));
        }

        return of(resultSet.columnHeaders().map(StoredColumnHeader::from), ImmutableList.fromCollection(storedRows));
    }


    @SuppressWarnings("unchecked")
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return (ImmutableList<MiniColumnHeader>) (ImmutableList<?>) columnHeaders;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<ImmutableList<MiniValue>> iterator() {
        return (Iterator<ImmutableList<MiniValue>>) (Iterator<?>) rows.iterator();
    }

    @SuppressWarnings("unchecked")
    public ImmutableList<ImmutableList<MiniValue>> rows() {
        return (ImmutableList<ImmutableList<MiniValue>>) (ImmutableList<?>) rows;
    }

}
