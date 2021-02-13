package hu.webarticum.miniconnect.util.manager;

import java.io.IOException;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniConnection;

public interface Driver {

    public String version();
    
    public boolean canAccept(String url);

    public MiniConnection openConnection(String url, Map<?, ?> properties) throws IOException;
    
}
