package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator.MultiComparatorBuilder;

public class ScanningTableIndex implements TableIndex {
    
    private final Table table;
    
    private final String name;
    
    private final ImmutableList<String> columnNames;
    
    private final ImmutableList<Integer> columnIndexes;
    
    
    public ScanningTableIndex(Table table, String name, ImmutableList<String> columnNames) {
        this.table = table;
        this.name = name;
        this.columnNames = columnNames;
        this.columnIndexes = collectColumnIndexes(table, columnNames);
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
    public TableSelection findMulti(
            ImmutableList<?> from,
            InclusionMode fromInclusionMode,
            ImmutableList<?> to,
            InclusionMode toInclusionMode,
            ImmutableList<NullsMode> nullsModes,
            ImmutableList<SortMode> sortModes) {
        boolean fromInclusive = fromInclusionMode == InclusionMode.INCLUDE;
        boolean toInclusive = toInclusionMode == InclusionMode.INCLUDE;
        boolean sort = !sortModes.filter(m -> m != SortMode.UNSORTED).isEmpty();
        MultiComparator multiComparator = createMultiComparator(table, columnNames, sortModes);
        
        List<SortHelper> foundEntries = collectEntries(
                from, fromInclusive, to, toInclusive, nullsModes, multiComparator);
        
        if (sort) {
            Collections.sort(foundEntries, Comparator.comparing(e -> e.values, multiComparator));
        }

        ImmutableList<BigInteger> rowIndexes = foundEntries.stream()
                .map(e -> e.index)
                .collect(ImmutableList.createCollector());
        return new SimpleSelection(rowIndexes);
    }

    private MultiComparator createMultiComparator(
            Table table,
            ImmutableList<String> columnNames,
            ImmutableList<SortMode> sortModes) {
        int size = columnNames.size();
        ImmutableList<SortMode> extendedSortModes = sortModes.resize(size, i -> SortMode.UNSORTED);
        NamedResourceStore<Column> columns = table.columns();
        MultiComparatorBuilder builder = MultiComparator.builder();
        for (int i = 0; i < size; i++) {
            String columnName = columnNames.get(i);
            SortMode sortMode = extendedSortModes.get(i);
            ColumnDefinition columnDefinition = columns.get(columnName).definition();
            Comparator<?> columnComparator = columnDefinition.comparator();
            boolean nullable = columnDefinition.isNullable();
            builder.add(columnComparator, nullable, sortMode.isAsc(), sortMode.isNullsFirst());
        }
        return builder.build();
    }

    private List<SortHelper> collectEntries(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive,
            ImmutableList<NullsMode> nullsModes,
            MultiComparator multiComparator) {
        BigInteger tableSize = table.size();
        List<SortHelper> foundRowEntries = new ArrayList<>();
        boolean fromAndToAreEqual = areEqual(from, to, multiComparator);
        for (
                BigInteger i = BigInteger.ZERO;
                i.compareTo(tableSize) < 0;
                i = i.add(BigInteger.ONE)) {
            ImmutableList<Object> row = table.row(i).getAll();
            ImmutableList<Object> values = extractValues(row);
            boolean isRowSelected = checkValues(
                    values,
                    from,
                    fromInclusive,
                    to,
                    toInclusive,
                    fromAndToAreEqual,
                    nullsModes,
                    multiComparator);
            if (isRowSelected) {
                foundRowEntries.add(new SortHelper(i, values));
            }
        }
        return foundRowEntries;
    }
    
    private boolean areEqual(
            ImmutableList<?> from, ImmutableList<?> to, MultiComparator multiComparator) {
        if (from == to) {
            return true;
        }
        if (from == null || to == null) {
            return false;
        }
        int fromSize = from.size();
        if (fromSize != to.size() || fromSize != columnNames.size()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        int cmp = multiComparator.compare((ImmutableList<Object>) from, (ImmutableList<Object>) to);
        return cmp == 0;
    }
    
    private ImmutableList<Object> extractValues(ImmutableList<Object> row) {
        List<Object> resultBuilder = new ArrayList<>(columnIndexes.size());
        for (int columnIndex : columnIndexes) {
            Object value = row.get(columnIndex);
            resultBuilder.add(value);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }
    
    private boolean checkValues(
            ImmutableList<Object> values,
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive,
            boolean fromAndToAreEqual,
            ImmutableList<NullsMode> nullsModes,
            MultiComparator multiComparator) {
        if (!checkNulls(values, nullsModes)) {
            return false;
        }
        
        if (fromAndToAreEqual) {
            if (from == null) {
                return true;
            }
            if (!fromInclusive || !toInclusive) {
                return false;
            }
            return isPrefixOf(from, values);
        }
        
        if (!checkFrom(values, from, fromInclusive, multiComparator)) {
            return false;
        }

        return checkTo(values, to, toInclusive, multiComparator);
    }
    
    private boolean checkNulls(ImmutableList<Object> values, ImmutableList<NullsMode> nullsModes) {
        int size = Math.min(values.size(), nullsModes.size());
        for (int i = 0; i < size; i++) {
            if (nullsModes.get(i) == NullsMode.NO_NULLS && values.get(i) == null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFrom(
            ImmutableList<Object> values,
            ImmutableList<?> from,
            boolean fromInclusive,
            MultiComparator multiComparator) {
        return checkBound(values, from, fromInclusive, true, multiComparator);
    }

    private boolean checkTo(
            ImmutableList<Object> values,
            ImmutableList<?> to,
            boolean toInclusive,
            MultiComparator multiComparator) {
        return checkBound(values, to, toInclusive, false, multiComparator);
    }
    
    private boolean checkBound(
            ImmutableList<Object> values,
            ImmutableList<?> bound,
            boolean boundInclusive,
            boolean isFrom,
            MultiComparator multiComparator) {
        
        if (bound == null) {
            return true;
        }
        
        int valuesSize = values.size();
        int boundSize = bound.size();
        
        if (boundSize > valuesSize) {
            return checkBound(
                    values, bound.section(0, valuesSize), boundInclusive, isFrom, multiComparator);
        }
        
        if (boundInclusive && isPrefixOf(bound, values)) {
            return true;
        }
        
        ImmutableList<Object> leftValues = values.section(0, boundSize);
        @SuppressWarnings("unchecked")
        ImmutableList<Object> comparableTo = (ImmutableList<Object>) bound;
        int cmp = multiComparator.compare(comparableTo, leftValues);
        
        return isFrom ? (cmp < 0) : (cmp > 0);
    }
    
    private boolean isPrefixOf(ImmutableList<?> prefix, ImmutableList<?> values) {
        int prefixWidth = prefix.size();
        if (prefixWidth > values.size()) {
            return false;
        }
        
        NamedResourceStore<Column> columns = table.columns();
        for (int i = 0; i < prefixWidth; i++) {
            @SuppressWarnings("unchecked")
            Comparator<Object> comparator =
                    (Comparator<Object>) columns.get(columnNames.get(i)).definition().comparator();
            Object prefixValue = prefix.get(i);
            Object value = values.get(i);
            if (
                    ((value == null || prefixValue == null) && value != prefixValue) ||
                    comparator.compare(value, prefixValue) != 0) {
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
