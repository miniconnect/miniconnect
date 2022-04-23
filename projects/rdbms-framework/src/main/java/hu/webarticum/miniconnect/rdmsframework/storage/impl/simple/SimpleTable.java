package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
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

    
    private SimpleTable(SimpleTableBuilder builder) {
        this.name = builder.name;
        this.writable = builder.writable;
        this.columnNames = ImmutableList.fromCollection(builder.columnDefinitions.keySet());
        this.columnDefinitions = ImmutableMap.fromMap(builder.columnDefinitions);
        this.indexNames = ImmutableList.fromCollection(builder.indexes.keySet());
        this.indexColumnNames = ImmutableMap.fromMap(builder.indexes);
        this.rows.addAll(builder.rows);
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
    public BigInteger size() {
        return BigInteger.valueOf(rows.size());
    }

    @Override
    public synchronized Row row(BigInteger rowIndex) {
        return new SimpleRow(columnStore.names(), rows.get(rowIndex.intValue()));
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public synchronized void applyPatch(TablePatch patch) {
        if (!writable) {
            throw new UnsupportedOperationException("This table is read-only");
        }
        
        rows.addAll(patch.insertedRows());
        
        for (Map.Entry<BigInteger, ImmutableMap<Integer, Object>> entry :
                patch.updates().entrySet()) {
            int rowIndex = entry.getKey().intValueExact();
            ImmutableMap<Integer, Object> rowUpdates = entry.getValue();
            ImmutableList<Object> currentRow = rows.get(rowIndex);
            ImmutableList<Object> updatedRow = currentRow.map(rowUpdates::getOrDefault);
            rows.set(rowIndex, updatedRow);
        }
        
        Iterator<BigInteger> deletionsIterator = patch.deletions().descendingIterator();
        while (deletionsIterator.hasNext()) {
            int deletedRowIndex = deletionsIterator.next().intValueExact();
            rows.remove(deletedRowIndex);
        }
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
            return new SimpleColumn(name);
        }
        
    }
    
    
    private class SimpleColumn implements Column {
        
        private final String name;
        
        private final int columnIndex;
        
        
        private SimpleColumn(String name) {
            this.name = name;
            this.columnIndex = columnNames.indexOf(name);
            
            if (this.columnIndex == -1) {
                throw new IllegalArgumentException("Unknown column: " + name);
            }
        }


        @Override
        public String name() {
            return name;
        }

        @Override
        public ColumnDefinition definition() {
            return columnDefinitions.get(name);
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
            return new ScanningTableIndex(SimpleTable.this, name, indexColumnNames.get(name));
        }
        
    }
    
    
    public static final class SimpleTableBuilder {

        private String name = "data";
        
        private boolean writable = true;
        
        private Map<String, ColumnDefinition> columnDefinitions = new LinkedHashMap<>();
        
        private Map<String, ImmutableList<String>> indexes = new LinkedHashMap<>();
        
        private final List<ImmutableList<Object>> rows = new ArrayList<>();
        
        
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

        public SimpleTable build() {
            return new SimpleTable(this);
        }
        
    }

}
