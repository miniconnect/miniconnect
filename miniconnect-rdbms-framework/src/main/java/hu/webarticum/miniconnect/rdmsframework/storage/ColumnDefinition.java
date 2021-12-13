package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Comparator;

public interface ColumnDefinition {

    public boolean isNullable();
    
    public String sqlType(); // FIXME
    
    public Class<?> javaType();
    
    public Comparator<?> comparator(); // NOSONAR
    
    // TODO: default value/generator, value restrictions
    
}
