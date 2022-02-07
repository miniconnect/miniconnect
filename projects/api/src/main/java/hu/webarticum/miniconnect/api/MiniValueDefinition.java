package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public interface MiniValueDefinition {
    
    public static final int DYNAMIC_SIZE = -1;
    

    public String type();
    
    public int size();

    public ImmutableMap<String, ByteString> properties();
}
