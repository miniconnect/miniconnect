package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Comparator;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.util.ComparatorUtil;

public class SimpleColumnDefinition implements ColumnDefinition {
    
    private final Class<?> clazz;
    
    private final boolean nullable;
    
    private final boolean autoIncremented;
    
    private final Comparator<?> comparator;
    

    public SimpleColumnDefinition() {
        this(Object.class);
    }
    
    public SimpleColumnDefinition(Class<?> clazz) {
        this(clazz, true);
    }
    
    public SimpleColumnDefinition(Class<?> clazz, boolean nullable) {
        this(clazz, nullable, false);
    }

    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, boolean autoIncremented) {
        this(clazz, nullable, autoIncremented, null);
    }
    
    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, boolean autoIncremented, Comparator<?> comparator) {
        this.clazz = clazz;
        this.nullable = nullable;
        this.autoIncremented = autoIncremented;
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
    public boolean isAutoIncremented() {
        return autoIncremented;
    }

    @Override
    public Comparator<?> comparator() {
        return comparator;
    }

}
