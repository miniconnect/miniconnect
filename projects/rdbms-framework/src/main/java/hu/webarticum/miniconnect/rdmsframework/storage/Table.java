package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface Table extends NamedResource {

    public NamedResourceStore<Column> columns();

    public NamedResourceStore<TableIndex> indexes();
    
    public BigInteger size();

    public Row row(BigInteger rowIndex);

    public boolean isWritable();

    public void applyPatch(TablePatch patch);

    public Sequence sequence();
    
}
