package hu.webarticum.miniconnect.api;

import java.io.Closeable;

public interface MiniResultSet extends Closeable {

    public boolean isClosed();
    
}
