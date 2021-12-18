package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class ScanningTableIndex implements TableIndex {
    
    private final Table table;
    
    private final String name;
    
    private final ImmutableList<String> columnNames;
    
    private final ImmutableList<Integer> columnIndexes;
    
    private final MultiComparator multiComparator;
    
    
    public ScanningTableIndex(Table table, String name, ImmutableList<String> columnNames) {
        this.table = table;
        this.name = name;
        this.columnNames = columnNames;
        this.columnIndexes = collectColumnIndexes(table, columnNames);
        this.multiComparator = createMultiComparator(table, columnNames);
    }
    
    private static ImmutableList<Integer> collectColumnIndexes(
            Table table,
            ImmutableList<String> columnNames) {
        ImmutableList<String> tableColumnNames = table.columns().names();
        int width = columnNames.size();
        Integer[] indexes = new Integer[width];
        for (int i = 0; i < width; i++) {
            String columnName = columnNames.get(i);
            int columnIndex = tableColumnNames.indexOf(columnName);
            if (columnIndex == -1) {
                throw new IllegalArgumentException(
                        String.format("Column not found: '%s'", columnName));
            }
            indexes[i] = columnIndex;
        }
        return ImmutableList.of(indexes);
    }

    private static MultiComparator createMultiComparator(
            Table table,
            ImmutableList<String> columnNames) {
        NamedResourceStore<Column> columns = table.columns();
        List<Comparator<?>> comparators = new ArrayList<>(columnNames.size());
        for (String columnName : columnNames) {
            Comparator<?> comparator = columns.get(columnName).definition().comparator();
            comparators.add(comparator);
        }
        return new MultiComparator(comparators);
    }
    

    public Table table() {
        return table;
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public ImmutableList<String> columnNames() {
        return columnNames;
    }

    @Override
    public boolean isUnique() {
        return false;
    }
    
    @Override
    public TableSelection find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive,
            boolean sort) {
        
        List<SortHelper> foundEntries = collectEntries(from, fromInclusive, to, toInclusive);
        
        if (sort) {
            Collections.sort(foundEntries, Comparator.comparing(e -> e.values, multiComparator));
        }

        ImmutableList<BigInteger> rowIndexes = new ImmutableList<>(foundEntries).map(e -> e.index);
        if (sort) {
            return new SimpleSelection(rowIndexes);
        } else {
            return new SimpleSelection(
                    table.rowOrderKey(),
                    table.reverseRowOrderKey(),
                    rowIndexes,
                    rowIndexes);
        }
    }
    
    private List<SortHelper> collectEntries(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive) {
        BigInteger tableSize = table.size();
        List<SortHelper> foundRowEntries = new ArrayList<>();
        for (
                BigInteger i = BigInteger.ZERO;
                i.compareTo(tableSize) < 0;
                i = i.add(BigInteger.ONE)) {
            ImmutableList<Object> row = table.row(i);
            ImmutableList<Object> values = extractValues(row);
            if (checkValues(values, from, fromInclusive, to, toInclusive)) { // FIXME
                foundRowEntries.add(new SortHelper(i, values));
            }
        }
        return foundRowEntries;
    }
    
    private ImmutableList<Object> extractValues(ImmutableList<Object> row) {
        List<Object> resultBuilder = new ArrayList<>(columnIndexes.size());
        for (int columnIndex : columnIndexes) {
            Object value = row.get(columnIndex);
            resultBuilder.add(value);
        }
        return new ImmutableList<>(resultBuilder);
    }
    
    private boolean checkValues(
            ImmutableList<Object> values,
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive) {
        if (from.equals(to)) { // FIXME: handle find(single), else use comparator
            return isPrefixOf(from, values);
        }
        
        // TODO
        return false;
    }
    
    private boolean isPrefixOf(ImmutableList<?> prefix, ImmutableList<?> values) {
        int prefixWidth = prefix.size();
        if (prefixWidth > values.size()) {
            return false;
        }
        
        for (int i = 0; i < prefixWidth; i++) {
            Object prefixValue = prefix.get(i);
            Object value = values.get(i);
            if (!Objects.equals(value, prefixValue)) { // FIXME: comparator?
                return false;
            }
        }
        
        return true;
    }
    
    
    private static class SortHelper {
        
        private final BigInteger index;
        
        private final ImmutableList<Object> values;

        
        private SortHelper(BigInteger index, ImmutableList<Object> values) {
            this.index = index;
            this.values = values;
        }

    }
    
}
