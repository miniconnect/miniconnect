package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

/**
 * Represents an ordered selection of rows in a table.
 * 
 * <p>
 * During the iteration <code>orderIndex</code> of entries must increase monotomically.
 * Two selections are mergeable
 * in the same transaction or non-transactional query execution
 * iff their <code>orderKey</code>s are equal.
 * </p>
 * 
 * @see OrderKey
 * @see TableSelection
 */
public interface TableSelection extends Iterable<TableSelectionEntry> {

    public OrderKey orderKey();
    
    public boolean containsRow(BigInteger rowIndex);
    
    public TableSelection reversed();
    
}
