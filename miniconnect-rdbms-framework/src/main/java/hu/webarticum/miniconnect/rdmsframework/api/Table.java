package hu.webarticum.miniconnect.rdmsframework.api;

import java.math.BigInteger;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface Table extends NamedResource {

    public NamedResourceStore<Column> columns();

    public NamedResourceStore<Index> indexes();
    
    public BigInteger size();
    
    public ImmutableList<Object> row(BigInteger rowIndex);
    
}
