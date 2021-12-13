package hu.webarticum.miniconnect.rdmsframework.storage.impl.fakecolumn;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;

// FIXME: create a proper column definition class
public class FakeColumnDefinition implements ColumnDefinition {

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

}
