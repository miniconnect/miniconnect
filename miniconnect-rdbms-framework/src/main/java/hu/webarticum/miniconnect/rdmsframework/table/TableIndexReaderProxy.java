package hu.webarticum.miniconnect.rdmsframework.table;

import hu.webarticum.miniconnect.rdmsframework.util.selection.Selection;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class TableIndexReaderProxy implements TableIndex {
    
    private final TableIndex baseIndex;
    

    public TableIndexReaderProxy(TableIndex baseIndex) {
        this.baseIndex = baseIndex;
    }
    
    
    @Override
    public String name() {
        return baseIndex.name();
    }

    @Override
    public ImmutableList<String> columnNames() {
        return baseIndex.columnNames();
    }

    @Override
    public boolean isUnique() {
        return baseIndex.isUnique();
    }

    @Override
    public Selection find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive) {
        return baseIndex.find(from, fromInclusive, to, toInclusive);
    }

}