package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.rdmsframework.storage.Constraint;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

// TODO
public class SimpleStorageAccess implements StorageAccess {

    private final SimpleResourceManager<Schema> schemaManager = new SimpleResourceManager<>();
    
    private final SimpleStorageAccessLockManager lockManager = new SimpleStorageAccessLockManager();
    

    @Override
    public SimpleStorageAccessLockManager lockManager() {
        return lockManager;
    }
    
    @Override
    public SimpleResourceManager<Schema> schemas() {
        return schemaManager;
    }

    @Override
    public NamedResourceStore<Constraint> constraints() {
        return new EmptyNamedResourceStore<>();
    }
    
}
