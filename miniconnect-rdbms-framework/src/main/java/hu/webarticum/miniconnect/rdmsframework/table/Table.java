package hu.webarticum.miniconnect.rdmsframework.table;

import java.math.BigInteger;

import hu.webarticum.miniconnect.rdmsframework.database.NamedResource;
import hu.webarticum.miniconnect.rdmsframework.database.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.database.TablePatch;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface Table extends NamedResource {

    public NamedResourceStore<Column> columns();

    public NamedResourceStore<Index> indexes();
    
    public BigInteger size();
    
    public ImmutableList<Object> row(BigInteger rowIndex);

    public boolean isWritable();

    public void applyPatch(TablePatch patch);
    
}
