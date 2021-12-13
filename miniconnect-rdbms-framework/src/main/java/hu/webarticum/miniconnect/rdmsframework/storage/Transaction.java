package hu.webarticum.miniconnect.rdmsframework.storage;

import java.io.Closeable;

public interface Transaction extends StorageAccess, Closeable {

    public void commit();
    
    public void rollback();
    
    @Override
    public default void close() {
        rollback();
    }
    
}
