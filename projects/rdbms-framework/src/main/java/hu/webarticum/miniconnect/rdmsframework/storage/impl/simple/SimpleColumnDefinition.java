package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Comparator;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;

// FIXME: create a proper column definition class
public class SimpleColumnDefinition implements ColumnDefinition {

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public String sqlType() {
        return "ANY";
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }
    
    @Override
    public Comparator<?> comparator() {
        return Comparator.naturalOrder();
    }

}
