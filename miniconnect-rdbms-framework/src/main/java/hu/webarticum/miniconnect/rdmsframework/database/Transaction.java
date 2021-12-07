package hu.webarticum.miniconnect.rdmsframework.database;

import java.io.Closeable;

public interface Transaction extends StorageAccess, Closeable {

    public void commit();
    
    public void rollback();
    
    @Override
    public default void close() {
        rollback();
    }
    
}
