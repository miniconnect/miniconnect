package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Comparator;
import java.util.Optional;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.util.ComparatorUtil;

public class SimpleColumnDefinition implements ColumnDefinition {
    
    private final Class<?> clazz;

    private final boolean nullable;

    private final boolean unique;
    
    private final boolean autoIncremented;
    
    private final Optional<ImmutableList<Object>> enumValues;
    
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

    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, boolean unique) {
        this(clazz, nullable, unique, false);
    }

    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, boolean unique, boolean autoIncremented) {
        this(clazz, nullable, unique, autoIncremented, null);
    }

    public SimpleColumnDefinition(
            Class<?> clazz,
            boolean nullable,
            boolean unique,
            boolean autoIncremented,
            ImmutableList<Object> enumValues) {
        this(clazz, nullable, unique, autoIncremented, enumValues, null);
    }

    public SimpleColumnDefinition(
            Class<?> clazz,
            boolean nullable,
            boolean unique,
            boolean autoIncremented,
            ImmutableList<Object> enumValues,
            Comparator<?> comparator) {
        this.clazz = clazz;
        this.nullable = nullable;
        this.unique = unique;
        this.autoIncremented = autoIncremented;
        this.enumValues = Optional.ofNullable(enumValues);
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
    public boolean isUnique() {
        return unique;
    }
    
    @Override
    public boolean isAutoIncremented() {
        return autoIncremented;
    }

    @Override
    public Optional<ImmutableList<Object>> enumValues() {
        return enumValues;
    }

    @Override
    public Comparator<?> comparator() {
        return comparator;
    }

}
