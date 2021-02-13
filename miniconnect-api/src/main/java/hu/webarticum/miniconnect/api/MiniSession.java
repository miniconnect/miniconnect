package hu.webarticum.miniconnect.api;

import java.io.Closeable;

public interface MiniSession extends Closeable {

    // FIXME: IOException?
    public MiniResult execute(String query);
    
    //public void putLargeData(String variable, InputStream dataSource);
    
    public boolean isClosed();
    
}
