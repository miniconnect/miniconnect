package hu.webarticum.miniconnect.rdmsframework.table.impl.simple;

import java.math.BigInteger;
import java.util.Comparator;

import hu.webarticum.miniconnect.rdmsframework.database.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.table.PatchableTableIndex;
import hu.webarticum.miniconnect.rdmsframework.table.Table;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class SimplePatchableTableIndex implements PatchableTableIndex {
    
    private final Table table;
    
    private final String name;
    
    private final ImmutableList<String> columnNames;
    
    private final Comparator<ImmutableList<Object>> comparator;
    
    
    public SimplePatchableTableIndex(
            Table table,
            String name,
            ImmutableList<String> columnNames,
            Comparator<ImmutableList<Object>> comparator) {
        this.table = table;
        this.name = name;
        this.columnNames = columnNames;
        this.comparator = comparator;
    }
    

    public Table table() {
        return table;
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public ImmutableList<String> columnNames() {
        return columnNames;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public Iterable<BigInteger> find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive) {
        
        // TODO
        return null;
        
    }
    
    @Override
    public void applyPatch(TablePatch patch) {
        
        // TODO
        
    }
    
}
