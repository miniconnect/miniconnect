package hu.webarticum.miniconnect.tool.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValueDefinition;

public class StoredColumnHeader implements MiniColumnHeader, Serializable {

    private static final long serialVersionUID = 1L;
    

    private final String name;
    
    private final StoredValueDefinition valueDefinition;
    
    
    public StoredColumnHeader(String name, MiniValueDefinition valueDefinition) {
        this.name = name;
        this.valueDefinition = StoredValueDefinition.of(valueDefinition);
    }

    public static StoredColumnHeader of(MiniColumnHeader columnHeader) {
        if (columnHeader instanceof StoredColumnHeader) {
            return (StoredColumnHeader) columnHeader;
        }
        
        return new StoredColumnHeader(columnHeader.name(), columnHeader.valueDefinition());
    }
    

    @Override
    public String name() {
        return name;
    }

    @Override
    public MiniValueDefinition valueDefinition() {
        return valueDefinition;
    }

}
