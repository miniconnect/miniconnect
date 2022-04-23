package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public abstract class AbstractTableDecorator implements Table {
    
    protected final Table baseTable;
    

    protected AbstractTableDecorator(Table baseTable) {
        this.baseTable = baseTable;
    }
    

    @Override
    public String name() {
        return baseTable.name();
    }

    @Override
    public NamedResourceStore<Column> columns() {
        return baseTable.columns();
    }

    @Override
    public NamedResourceStore<TableIndex> indexes() {
        return baseTable.indexes();
    }

    @Override
    public BigInteger size() {
        return baseTable.size();
    }

    @Override
    public Row row(BigInteger rowIndex) {
        return baseTable.row(rowIndex);
    }

    @Override
    public boolean isWritable() {
        return baseTable.isWritable();
    }

    @Override
    public void applyPatch(TablePatch patch) {
        baseTable.applyPatch(patch);
    }

}
