package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public class TableSelectionEntry {

    private final Object orderKey;
    
    private final BigInteger rowIndex;
    
    private final BigInteger orderIndex;

    
    public TableSelectionEntry(Object orderKey, BigInteger rowIndex, BigInteger orderIndex) {
        this.orderKey = orderKey;
        this.rowIndex = rowIndex;
        this.orderIndex = orderIndex;
    }


    public Object orderKey() {
        return orderKey;
    }

    public BigInteger tableIndex() {
        return rowIndex;
    }

    public BigInteger orderIndex() {
        return orderIndex;
    }
    
}