package hu.webarticum.miniconnect.rdmsframework.engine;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface EngineSession extends CheckableCloseable {
    
    public Engine engine();

    public StorageAccess storageAccess();
    
    // TODO: transaction management

}
