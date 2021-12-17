package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface TableSelection extends Iterable<TableSelectionEntry> {

    public Object orderKey();
    
    public boolean containsRow(BigInteger rowIndex);
    
    public TableSelection reverse();
    
}
