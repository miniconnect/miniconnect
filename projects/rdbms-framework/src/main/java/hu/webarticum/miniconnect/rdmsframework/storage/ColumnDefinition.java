package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Comparator;

// TODO: more functionality
public interface ColumnDefinition {

    public Class<?> clazz();
    
    public boolean isNullable();
    
    public boolean isAutoIncremented();
    
    // FIXME: Comparator<Object> ?
    public Comparator<?> comparator(); // NOSONAR
    
}
