package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Comparator;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;

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
        this(clazz, nullable, createDefaultComparatorFor(clazz));
    }
    
    public SimpleColumnDefinition(Class<?> clazz, boolean nullable, Comparator<?> comparator) {
        this.clazz = clazz;
        this.nullable = nullable;
        this.comparator = comparator;
    }
    
    private static Comparator<?> createDefaultComparatorFor(Class<?> clazz) {
        if (clazz == String.class) {
            return (String s1, String s2) -> s1.compareToIgnoreCase(s2);
        } else {
            return Comparator.naturalOrder();
        }
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
