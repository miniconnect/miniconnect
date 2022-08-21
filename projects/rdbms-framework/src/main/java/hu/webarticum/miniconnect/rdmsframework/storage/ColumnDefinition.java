package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Comparator;
import java.util.Optional;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface ColumnDefinition {

    public Class<?> clazz();
    
    public boolean isNullable();
    
    public boolean isUnique();
    
    public boolean isAutoIncremented();
    
    public Optional<ImmutableList<Object>> enumValues();
    
    // FIXME: Comparator<Object> ?
    public Comparator<?> comparator(); // NOSONAR
    
}
