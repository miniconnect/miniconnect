package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.IOException;

public interface MiniSession extends Closeable {

    // FIXME: IOException?
    public MiniResult execute(String query) throws IOException;
    
    //public void putLargeData(String variable, InputStream dataSource);
    
    public boolean isClosed();
    
}
