package hu.webarticum.miniconnect.api;

import java.io.InputStream;

public interface MiniValue {
    
    public boolean isNull();

    public int contentLength();
    
    // TODO: immutable access
    public byte[] content();
    
    public InputStream inputStream();
    
}
