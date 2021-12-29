package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

/**
 * Entry data class for {@link TableSelection} implementations.
 * 
 * <p>
 * Two entries are comparable and mergeable
 * in the same transaction or non-transactional query execution
 * iff their <code>orderKey</code>s are equal.
 * </p>
 * 
 * @see OrderKey
 * @see TableSelection
 */
public class TableSelectionEntry {

    private final OrderKey orderKey;
    
    private final BigInteger rowIndex;
    
    private final BigInteger orderIndex;

    
    public TableSelectionEntry(OrderKey orderKey, BigInteger rowIndex, BigInteger orderIndex) {
        this.orderKey = orderKey;
        this.rowIndex = rowIndex;
        this.orderIndex = orderIndex;
    }


    public OrderKey orderKey() {
        return orderKey;
    }

    public BigInteger tableIndex() {
        return rowIndex;
    }

    public BigInteger orderIndex() {
        return orderIndex;
    }
    
}
