package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.selection.SimpleSelection;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class ScanningTableIndex implements TableIndex {
    
    private final Table table;
    
    private final String name;
    
    private final ImmutableList<String> columnNames;
    
    
    public ScanningTableIndex(Table table, String name, ImmutableList<String> columnNames) {
        this.table = table;
        this.name = name;
        this.columnNames = columnNames;
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
    public TableSelection find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive,
            boolean sort) {
        
        // TODO
        return new SimpleSelection(new Object(), ImmutableList.empty());
        
    }
    
}
