package hu.webarticum.miniconnect.rdmsframework.table;

import hu.webarticum.miniconnect.rdmsframework.database.NamedResource;
import hu.webarticum.miniconnect.rdmsframework.util.selection.Selection;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface TableIndex extends NamedResource {
    
    public ImmutableList<String> columnNames();

    public boolean isUnique();

    public Selection find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive);

    public default Selection find(
            Object from, boolean fromInclusive, Object to, boolean toInclusive) {
        return find(ImmutableList.of(from), fromInclusive, ImmutableList.of(to), toInclusive);
    }

    public default Selection find(Object value) {
        return find(ImmutableList.of(value), true, ImmutableList.of(value), true);
    }
    
}
