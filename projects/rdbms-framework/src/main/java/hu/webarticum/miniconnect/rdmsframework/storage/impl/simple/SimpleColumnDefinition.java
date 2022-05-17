package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Comparator;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.util.ComparatorUtil;

public class SimpleColumnDefinition implements ColumnDefinition {
    
    private final boolean nullable;
    
    private final Class<?> clazz;
    
    private final Comparator<?> comparator;
    

    public SimpleColumnDefinition() {
        this(Object.class);
    }
    
    public SimpleColumnDefinition(Class<?> clazz) {
        this(clazz, true);
    }
    
    public SimpleColumnDefinition(Class<?> clazz, boolean nullable) {
        this(clazz, nullable, null);
    }
    
    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, Comparator<?> comparator) {
        this.clazz = clazz;
        this.nullable = nullable;
        this.comparator = comparator != null ? comparator : ComparatorUtil.createDefaultComparatorFor(clazz);
    }
    

    @Override
    public Class<?> clazz() {
        return clazz;
    }
    
    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public Comparator<?> comparator() {
        return comparator;
    }

}
