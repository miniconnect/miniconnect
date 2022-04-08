package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class TablePatch {
    
    private final List<ImmutableList<Object>> insertedRows;
    
    private final NavigableMap<BigInteger, ImmutableMap<Integer, Object>> updates;
    
    private final NavigableSet<BigInteger> deletions;

    
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

    public NavigableMap<BigInteger, ImmutableMap<Integer, Object>> updates() {
        return updates;
    }

    public NavigableSet<BigInteger> deletions() {
        return deletions;
    }
    
    
    public static class TablePatchBuilder {
        
        private final List<ImmutableList<Object>> insertedRows = new ArrayList<>();
        
        private final NavigableMap<BigInteger, ImmutableMap<Integer, Object>> updates =
                new TreeMap<>();
        
        private final NavigableSet<BigInteger> deletions = new TreeSet<>();
        
        private volatile boolean closed = false;
        
        
        private TablePatchBuilder() {
            // use builder()
        }
        
        
        public TablePatchBuilder insert(ImmutableList<Object> rowData) {
            checkClosed();
            insertedRows.add(rowData);
            return this;
        }

        public TablePatchBuilder update(BigInteger index, ImmutableMap<Integer, Object> rowUpdates) {
            checkClosed();
            updates.put(index, rowUpdates);
            return this;
        }

        public TablePatchBuilder delete(BigInteger index) {
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
