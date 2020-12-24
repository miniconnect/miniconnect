package hu.webarticum.miniconnect.api;

import java.io.Closeable;

public interface MiniResult extends Closeable {

    public boolean isSuccess();

    public MiniResultSet resultSet();
    
    public boolean isClosed();
    
    // TODO: status, error, warnings
    
}
