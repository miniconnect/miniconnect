package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class TablePatch {

    private final ImmutableList<Row> insertedRows;
    
    private final ImmutableMap<BigInteger, ImmutableMap<Integer, Object>> updates;
    
    private final ImmutableList<BigInteger> deletions;

    
    public TablePatch(
            ImmutableList<Row> insertedRows,
            ImmutableMap<BigInteger, ImmutableMap<Integer, Object>> updates,
            ImmutableList<BigInteger> deletions) {
        this.insertedRows = insertedRows;
        this.updates = updates;
        this.deletions = deletions;
    }


    public ImmutableList<Row> insertedRows() {
        return insertedRows;
    }

    public ImmutableMap<BigInteger, ImmutableMap<Integer, Object>> updates() {
        return updates;
    }

    public ImmutableList<BigInteger> deletions() {
        return deletions;
    }
    
}
