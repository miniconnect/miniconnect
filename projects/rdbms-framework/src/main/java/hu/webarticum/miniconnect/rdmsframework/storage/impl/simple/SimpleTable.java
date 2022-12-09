package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Sequence;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;

public class SimpleTable implements Table {
    
    private final String name;
    
    private final boolean writable;

    private final ImmutableList<String> columnNames;
    
    private final ImmutableMap<String, ColumnDefinition> columnDefinitions;

    private final ImmutableList<String> indexNames;
    
    private final ImmutableMap<String, ImmutableList<String>> indexColumnNames;
    
    private final List<ImmutableList<Object>> rows = new ArrayList<>();
    
    private final SimpleColumnStore columnStore = new SimpleColumnStore();
    
    private final SimpleTableIndexStore tableIndexStore = new SimpleTableIndexStore();
    
    private final SimpleSequence sequence;

    
    private SimpleTable(SimpleTableBuilder builder) {
        this.name = builder.name;
        this.writable = builder.writable;
        this.columnNames = ImmutableList.fromCollection(builder.columnDefinitions.keySet());
        this.columnDefinitions = ImmutableMap.fromMap(builder.columnDefinitions);
        this.indexNames = ImmutableList.fromCollection(builder.indexes.keySet());
        this.indexColumnNames = ImmutableMap.fromMap(builder.indexes);
        this.rows.addAll(builder.rows);
        this.sequence = new SimpleSequence(calculateSequenceValue(builder.sequenceValue, builder.rows));
    }
    
    private LargeInteger calculateSequenceValue(LargeInteger sequenceValue, List<ImmutableList<Object>> rows) {
        if (sequenceValue != null) {
            return sequenceValue;
        }
        
        // FIXME: find max id + 1?
        return LargeInteger.of(rows.size()).increment();
    }
    
    public static SimpleTableBuilder builder() {
        return new SimpleTableBuilder();
    }
    

    @Override
    public String name() {
        return name;
    }

    @Override
    public NamedResourceStore<Column> columns() {
        return columnStore;
    }

    @Override
    public NamedResourceStore<TableIndex> indexes() {
        return tableIndexStore;
    }

    @Override
    public LargeInteger size() {
        return LargeInteger.of(rows.size());
    }

    @Override
    public synchronized Row row(LargeInteger rowIndex) {
        return new SimpleRow(columnStore.names(), rows.get(rowIndex.intValue()));
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public synchronized void applyPatch(TablePatch patch) {
        checkWritable();
        checkNullsInPatch(patch);
        checkUniqueInPatch(patch);
        
        rows.addAll(patch.insertedRows());
        
        for (Map.Entry<LargeInteger, ImmutableMap<Integer, Object>> entry :
                patch.updates().entrySet()) {
            int rowIndex = entry.getKey().intValueExact();
            ImmutableMap<Integer, Object> rowUpdates = entry.getValue();
            ImmutableList<Object> currentRow = rows.get(rowIndex);
            ImmutableList<Object> updatedRow = currentRow.map(rowUpdates::getOrDefault);
            rows.set(rowIndex, updatedRow);
        }
        
        Iterator<LargeInteger> deletionsIterator = patch.deletions().descendingIterator();
        while (deletionsIterator.hasNext()) {
            int deletedRowIndex = deletionsIterator.next().intValueExact();
            rows.remove(deletedRowIndex);
        }
    }
    
    private void checkWritable() {
        if (!writable) {
            throw PredefinedError.TABLE_READONLY.toException(name);
        }
    }

    private void checkNullsInPatch(TablePatch patch) {
        Set<Integer> nonNullableColumnIndices = new HashSet<>();
        int columnCount = columnNames.size();
        for (int i = 0; i < columnCount; i++) {
            if (!columnDefinitions.get(columnNames.get(i)).isNullable()) {
                nonNullableColumnIndices.add(i);
            }
        }
        if (nonNullableColumnIndices.isEmpty()) {
            return;
        }
        
        for (ImmutableMap<Integer, Object> rowUpdates : patch.updates().values()) {
            for (Map.Entry<Integer, Object> updateEntry : rowUpdates.entrySet()) {
                Integer columnIndex = updateEntry.getKey();
                if (nonNullableColumnIndices.contains(columnIndex) && updateEntry.getValue() == null) {
                    String columnName = columnNames.get(columnIndex);
                    throw PredefinedError.COLUMN_VALUE_NULL.toException(columnName);
                }
            }
        }

        for (ImmutableList<Object> insertedRow : patch.insertedRows()) {
            for (Integer columnIndex : nonNullableColumnIndices) {
                if (insertedRow.get(columnIndex) == null) {
                    String columnName = columnNames.get(columnIndex);
                    throw PredefinedError.COLUMN_VALUE_NULL.toException(columnName);
                }
            }
        }
    }
    
    private void checkUniqueInPatch(TablePatch patch) {
        Map<Integer, Set<Object>> uniqueColumnValues = new HashMap<>();
        int columnCount = columnNames.size();
        for (int i = 0; i < columnCount; i++) {
            ColumnDefinition columnDefinition = columnDefinitions.get(columnNames.get(i));
            if (columnDefinition.isUnique()) {
                @SuppressWarnings("unchecked")
                Comparator<Object> comparator = (Comparator<Object>) columnDefinition.comparator();
                uniqueColumnValues.put(i, new TreeSet<>(comparator));
            }
        }
        if (uniqueColumnValues.isEmpty()) {
            return;
        }
        
        int rowCount = rows.size();
        for (int i = 0; i < rowCount; i++) {
            LargeInteger rowIndex = LargeInteger.of(i);
            ImmutableList<Object> row = rows.get(i);
            for (Map.Entry<Integer, Set<Object>> rowEntry : uniqueColumnValues.entrySet()) {
                Integer columnIndex = rowEntry.getKey();
                Set<Object> values = rowEntry.getValue();
                Object value = row.get(columnIndex);
                if (
                        value != null &&
                        !patch.deletions().contains(rowIndex) &&
                        (!patch.updates().containsKey(rowIndex) ||
                                !patch.updates().get(rowIndex).containsKey(columnIndex))) {
                    values.add(value);
                }
            }
        }
        
        for (ImmutableMap<Integer, Object> rowUpdates : patch.updates().values()) {
            for (Map.Entry<Integer, Object> updateEntry : rowUpdates.entrySet()) {
                checkAndAddUniqueValue(updateEntry.getKey(), updateEntry.getValue(), uniqueColumnValues);
            }
        }
        for (ImmutableList<Object> insertedRow : patch.insertedRows()) {
            for (Integer columnIndex : uniqueColumnValues.keySet()) {
                checkAndAddUniqueValue(columnIndex, insertedRow.get(columnIndex), uniqueColumnValues);
            }
        }
    }
    
    private void checkAndAddUniqueValue(
            int columnIndex, Object newValue, Map<Integer, Set<Object>> uniqueColumnValues) {
        if (newValue == null) {
            return;
        }
        
        Set<Object> values = uniqueColumnValues.get(columnIndex);
        if (values != null && !values.add(newValue)) {
            String columnName = columnNames.get(columnIndex);
            throw PredefinedError.COLUMN_VALUE_NOT_UNIQUE.toException(columnName, newValue);
        }
    }
    
    @Override
    public Sequence sequence() {
        return sequence;
    }

    
    private class SimpleColumnStore implements NamedResourceStore<Column> {

        @Override
        public ImmutableList<String> names() {
            return columnNames;
        }

        @Override
        public ImmutableList<Column> resources() {
            return columnNames.map(this::get);
        }

        @Override
        public boolean contains(String name) {
            return columnNames.contains(name);
        }
        
        @Override
        public Column get(String name) {
            return new SimpleColumn(name, columnDefinitions.get(name));
        }
        
    }
    

    private class SimpleTableIndexStore implements NamedResourceStore<TableIndex> {

        private final Map<String, TableIndex> cache = Collections.synchronizedMap(new HashMap<>());
        
        
        @Override
        public ImmutableList<String> names() {
            return indexNames;
        }

        @Override
        public ImmutableList<TableIndex> resources() {
            return indexNames.map(this::get);
        }

        @Override
        public boolean contains(String name) {
            return indexNames.contains(name);
        }
        
        @Override
        public TableIndex get(String name) {
            return cache.computeIfAbsent(name, this::createIndex);
        }
        
        private TableIndex createIndex(String name) {
            ImmutableList<String> columnNames = indexColumnNames.get(name);
            if (columnNames == null) {
                return null;
            }
            return new ScanningTableIndex(SimpleTable.this, name, columnNames);
        }
        
    }
    
    
    public static final class SimpleTableBuilder {

        private String name = "data";
        
        private boolean writable = true;
        
        private Map<String, ColumnDefinition> columnDefinitions = new LinkedHashMap<>();
        
        private Map<String, ImmutableList<String>> indexes = new LinkedHashMap<>();
        
        private final List<ImmutableList<Object>> rows = new ArrayList<>();

        private LargeInteger sequenceValue = null;
        
        
        public SimpleTableBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SimpleTableBuilder writable(boolean writable) {
            this.writable = writable;
            return this;
        }

        public SimpleTableBuilder columnDefinitions(
                ImmutableMap<String, ColumnDefinition> columnDefinitions) {
            return columnDefinitions(columnDefinitions.asMap());
        }
        
        public SimpleTableBuilder columnDefinitions(
                Map<String, ColumnDefinition> columnDefinitions) {
            this.columnDefinitions.clear();
            this.columnDefinitions.putAll(columnDefinitions);
            return this;
        }

        public SimpleTableBuilder addColumn(String name, ColumnDefinition columnDefinition) {
            this.columnDefinitions.put(name, columnDefinition);
            return this;
        }

        public SimpleTableBuilder addColumnWithIndex(
                String name, ColumnDefinition columnDefinition) {
            addColumn(name, columnDefinition);
            addIndex(name, ImmutableList.of(name));
            return this;
        }

        public SimpleTableBuilder indexes(ImmutableMap<String, ImmutableList<String>> indexes) {
            return indexes(indexes.asMap());
        }
        
        public SimpleTableBuilder indexes(Map<String, ImmutableList<String>> indexes) {
            this.indexes.clear();
            this.indexes.putAll(indexes);
            return this;
        }

        public SimpleTableBuilder addIndex(String name, ImmutableList<String> columnNames) {
            this.indexes.put(name, columnNames);
            return this;
        }

        public SimpleTableBuilder rows(Collection<ImmutableList<Object>> rows) {
            this.rows.clear();
            this.rows.addAll(rows);
            return this;
        }

        public SimpleTableBuilder addRow(ImmutableList<Object> row) {
            this.rows.add(row);
            return this;
        }

        public SimpleTableBuilder addRow(Collection<Object> row) {
            this.rows.add(ImmutableList.fromCollection(row));
            return this;
        }

        public SimpleTableBuilder sequenceValue(LargeInteger sequenceValue) {
            this.sequenceValue = sequenceValue;
            return this;
        }

        public SimpleTable build() {
            return new SimpleTable(this);
        }
        
    }

}
