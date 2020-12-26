package hu.webarticum.miniconnect.api;

import java.io.Closeable;

public interface MiniConnection extends Closeable {

    public MiniResult execute(String query);
    
    public boolean isClosed();
    
}
