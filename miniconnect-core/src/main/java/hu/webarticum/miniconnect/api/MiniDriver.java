package hu.webarticum.miniconnect.api;

import java.io.IOException;
import java.util.Map;

public interface MiniDriver {

    public String version();
    
    public boolean canAccept(String url);
    
    public MiniConnection openConnection(String url, Map<?, ?> properties) throws IOException;
    
}
