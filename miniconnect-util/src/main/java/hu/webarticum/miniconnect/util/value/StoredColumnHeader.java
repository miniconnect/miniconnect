package hu.webarticum.miniconnect.util.value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;

public class StoredColumnHeader implements MiniColumnHeader {

    private final String name;
    
    private final String type;
    
    private final Map<String, String> properties;
    

    public StoredColumnHeader(String name, String type) {
        this(name, type, false, new HashMap<>());
    }
    
    public StoredColumnHeader(String name, String type, Map<String, String> properties) {
        this(name, type, true, properties);
    }
    
    public StoredColumnHeader(
            String name, String type, boolean copyProperties, Map<String, String> properties) {
        
        this.name = name;
        this.type = type;
        this.properties = copyProperties ? new HashMap<>(properties) : properties;
    }
    

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Map<String, String> properties() {
        return Collections.unmodifiableMap(properties);
    }

}