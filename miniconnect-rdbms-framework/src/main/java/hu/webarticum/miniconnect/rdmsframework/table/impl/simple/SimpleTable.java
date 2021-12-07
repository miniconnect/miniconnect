package hu.webarticum.miniconnect.rdmsframework.table.impl.simple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.rdmsframework.database.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.database.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.table.Column;
import hu.webarticum.miniconnect.rdmsframework.table.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.table.Index;
import hu.webarticum.miniconnect.rdmsframework.table.Table;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public class SimpleTable implements Table {
    
    private final String name;
    
    private final boolean writable;
    
    private final ImmutableList<String> columnNames;
    
    private final ImmutableMap<String, ColumnDefinition> columnDefinitions;
    
    private final List<ImmutableList<Object>> rows = new ArrayList<>();
    
    private final SimpleColumnStore columnStore = new SimpleColumnStore();
    
    
    public SimpleTable() {
        this(new SimpleTableBuilder());
    }
    
    private SimpleTable(SimpleTableBuilder builder) {
        this.name = builder.name;
        this.writable = builder.writable;
        this.columnNames = new ImmutableList<>(builder.columnDefinitions.keySet());
        this.columnDefinitions = new ImmutableMap<>(builder.columnDefinitions);
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
    public NamedResourceStore<Index> indexes() {
        
        // TODO
        return null;
        
    }

    @Override
    public BigInteger size() {
        return BigInteger.valueOf(rows.size());
    }

    @Override
    public synchronized ImmutableList<Object> row(BigInteger rowIndex) {
        return rows.get(rowIndex.intValue());
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
        
        // TODO
        
    }

    
    private class SimpleColumnStore implements NamedResourceStore<Column> {

        @Override
        public ImmutableList<String> names() {
            return columnNames;
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
        }


        @Override
        public String name() {
            return name;
        }

        @Override
        public ColumnDefinition definition() {
            return columnDefinitions.get(name);
        }

        @Override
        public Object get(BigInteger rowIndex) {
            return row(rowIndex).get(columnIndex);
        }
        
    }
    
    
    public static final class SimpleTableBuilder {

        private String name = "data";
        
        private boolean writable = true;
        
        private Map<String, ColumnDefinition> columnDefinitions = new LinkedHashMap<>();
        
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
                Map<String, ColumnDefinition> columnDefinitions) {
            this.columnDefinitions.clear();
            this.columnDefinitions.putAll(columnDefinitions);
            return this;
        }

        public SimpleTableBuilder addColumn(String name, ColumnDefinition columnDefinition) {
            this.columnDefinitions.put(name, columnDefinition);
            return this;
        }

        public SimpleTableBuilder rows(Collection<ImmutableList<Object>> rows) {
            this.rows.clear();
            this.rows.addAll(rows);
            return this;
        }

        public SimpleTableBuilder appendRow(ImmutableList<Object> row) {
            this.rows.add(row);
            return this;
        }

        public SimpleTableBuilder appendRow(Collection<Object> row) {
            this.rows.add(new ImmutableList<>(row));
            return this;
        }

        public SimpleTable build() {
            return new SimpleTable(this);
        }
        
    }

}
