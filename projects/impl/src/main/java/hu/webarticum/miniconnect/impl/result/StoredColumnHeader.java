package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValueDefinition;

public final class StoredColumnHeader implements MiniColumnHeader, Serializable {

    private static final long serialVersionUID = 1L;


    private final String name;

    private final boolean isNullable;

    private final StoredValueDefinition valueDefinition;


    private StoredColumnHeader(String name, boolean isNullable, StoredValueDefinition valueDefinition) {
        this.name = name;
        this.isNullable = isNullable;
        this.valueDefinition = valueDefinition;
    }

    public static StoredColumnHeader of(String name, boolean isNullable, StoredValueDefinition valueDefinition) {
        return new StoredColumnHeader(name, isNullable, valueDefinition);
    }

    public static StoredColumnHeader from(String name, boolean isNullable, MiniValueDefinition valueDefinition) {
        return of(name, isNullable, StoredValueDefinition.from(valueDefinition));
    }

    public static StoredColumnHeader from(MiniColumnHeader columnHeader) {
        if (columnHeader instanceof StoredColumnHeader) {
            return (StoredColumnHeader) columnHeader;
        }

        return from(columnHeader.name(), columnHeader.isNullable(), columnHeader.valueDefinition());
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public MiniValueDefinition valueDefinition() {
        return valueDefinition;
    }

}
