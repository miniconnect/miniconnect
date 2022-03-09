package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface Table extends NamedResource {

    public NamedResourceStore<Column> columns();

    public NamedResourceStore<TableIndex> indexes();
    
    public BigInteger size();
    
    public ImmutableList<Object> row(BigInteger rowIndex);
    
    public boolean isWritable();

    public void applyPatch(TablePatch patch);
    
}
