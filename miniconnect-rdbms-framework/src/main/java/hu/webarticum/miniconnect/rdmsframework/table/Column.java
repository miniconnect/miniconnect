package hu.webarticum.miniconnect.rdmsframework.table;

import java.math.BigInteger;

import hu.webarticum.miniconnect.rdmsframework.database.NamedResource;

public interface Column extends NamedResource {

    public ColumnDefinition definition();
    
    public Object get(BigInteger rowIndex);
    
}
