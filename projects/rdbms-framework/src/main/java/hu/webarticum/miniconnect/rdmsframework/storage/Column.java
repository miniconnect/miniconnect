package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface Column extends NamedResource {

    public ColumnDefinition definition();
    
    public Object get(BigInteger rowIndex);
    
}
