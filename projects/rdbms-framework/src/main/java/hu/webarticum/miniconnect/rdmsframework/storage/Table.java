package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.LargeInteger;

public interface Table extends NamedResource {

    public NamedResourceStore<Column> columns();

    public NamedResourceStore<TableIndex> indexes();
    
    public LargeInteger size();

    public Row row(LargeInteger rowIndex);

    public boolean isWritable();

    public void applyPatch(TablePatch patch);

    public Sequence sequence();
    
}
