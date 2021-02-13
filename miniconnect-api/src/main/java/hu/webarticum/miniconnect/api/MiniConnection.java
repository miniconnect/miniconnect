package hu.webarticum.miniconnect.api;

import java.io.Closeable;

public interface MiniConnection extends Closeable {
    
    public MiniSession openSession();

    public boolean isClosed();
    
}
