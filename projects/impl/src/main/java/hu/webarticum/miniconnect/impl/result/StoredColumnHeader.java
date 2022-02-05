package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValueDefinition;

public final class StoredColumnHeader implements MiniColumnHeader, Serializable {

    private static final long serialVersionUID = 1L;
    

    private final String name;
    
    private final boolean isNullable;
    
    private final StoredValueDefinition valueDefinition;
    
    
    public StoredColumnHeader(
            String name,
            boolean isNullable,
            MiniValueDefinition valueDefinition) {
        this.name = name;
        this.isNullable = isNullable;
        this.valueDefinition = StoredValueDefinition.of(valueDefinition);
    }

    public static StoredColumnHeader of(MiniColumnHeader columnHeader) {
        if (columnHeader instanceof StoredColumnHeader) {
            return (StoredColumnHeader) columnHeader;
        }
        
        return new StoredColumnHeader(
                columnHeader.name(),
                columnHeader.isNullable(),
                columnHeader.valueDefinition());
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
