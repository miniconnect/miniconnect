package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.util.data.ImmutableList;

// TODO: add some support for group by
public interface TableIndex extends NamedResource {
    
    public ImmutableList<String> columnNames();

    public boolean isUnique();

    public TableSelection find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive,
            boolean sort);

    public default TableSelection find(ImmutableList<?> values) {
        return find(values, true, values, true, false);
    }
    
    public default TableSelection findValue(Object value) {
        return find(ImmutableList.of(value));
    }
    
}
