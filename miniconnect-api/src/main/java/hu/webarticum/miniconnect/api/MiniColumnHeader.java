package hu.webarticum.miniconnect.api;

import java.util.Map;

public interface MiniColumnHeader {

    public String name();

    public String type();
    
    // TODO: size() ?

    public Map<String, String> properties();
    
}
