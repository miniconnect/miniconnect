package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.LargeInteger;

public interface TableSelection extends Iterable<LargeInteger> {

    public boolean containsRow(LargeInteger rowIndex);
    
}
