package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Optional;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface Column extends NamedResource {

    public ColumnDefinition definition();
    
    public default Optional<ImmutableList<Object>> possibleValues() {
        return Optional.empty();
    }
    
}
