package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Comparator;

// TODO: more functionality
public interface ColumnDefinition {

    public Class<?> clazz();
    
    public boolean isNullable();
    
    public Comparator<?> comparator(); // NOSONAR
    
}
