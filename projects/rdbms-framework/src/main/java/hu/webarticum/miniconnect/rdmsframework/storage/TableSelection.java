package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface TableSelection extends Iterable<TableSelectionEntry> {

    public OrderKey orderKey();
    
    public boolean containsRow(BigInteger rowIndex);
    
    public TableSelection reversed();
    
}
