package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;

public class TablePatch {
    
    private final List<ImmutableList<Object>> insertedRows;
    
    private final NavigableMap<LargeInteger, ImmutableMap<Integer, Object>> updates;
    
    private final NavigableSet<LargeInteger> deletions;

    
    private TablePatch(TablePatchBuilder builder) {
        this.insertedRows =  Collections.unmodifiableList(builder.insertedRows);
        this.updates = Collections.unmodifiableNavigableMap(builder.updates);
        this.deletions = Collections.unmodifiableNavigableSet(builder.deletions);
    }
    
    public static TablePatchBuilder builder() {
        return new TablePatchBuilder();
    }


    public List<ImmutableList<Object>> insertedRows() {
        return insertedRows;
    }

    public NavigableMap<LargeInteger, ImmutableMap<Integer, Object>> updates() {
        return updates;
    }

    public NavigableSet<LargeInteger> deletions() {
        return deletions;
    }
    
    
    public static class TablePatchBuilder {
        
        private final List<ImmutableList<Object>> insertedRows = new ArrayList<>();
        
        private final NavigableMap<LargeInteger, ImmutableMap<Integer, Object>> updates =
                new TreeMap<>();
        
        private final NavigableSet<LargeInteger> deletions = new TreeSet<>();
        
        private volatile boolean closed = false;
        
        
        private TablePatchBuilder() {
            // use builder()
        }
        
        
        public TablePatchBuilder insert(ImmutableList<Object> rowData) {
            checkClosed();
            insertedRows.add(rowData);
            return this;
        }

        public TablePatchBuilder update(LargeInteger index, ImmutableMap<Integer, Object> rowUpdates) {
            checkClosed();
            updates.put(index, rowUpdates);
            return this;
        }

        public TablePatchBuilder delete(LargeInteger index) {
            checkClosed();
            deletions.add(index);
            return this;
        }
        
        private void checkClosed() {
            if (closed) {
                throw new IllegalStateException("This builder was already closed");
            }
        }
        
        
        public synchronized TablePatch build() {
            closed = true;
            return new TablePatch(this);
        }

    }
    
}
