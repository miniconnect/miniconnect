package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.util.List;

public interface MiniResult extends Closeable {

    public boolean isSuccess(); // FIXME: status? exception?
    
    public String errorMessage(); // FIXME: error code? etc.?
    
    public List<String> warnings();

    public MiniResultSet resultSet();
    
    public boolean isClosed();
    
}
