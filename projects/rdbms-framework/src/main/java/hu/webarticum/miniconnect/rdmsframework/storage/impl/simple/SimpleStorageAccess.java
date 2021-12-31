package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.rdmsframework.storage.Constraint;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

// TODO
public class SimpleStorageAccess implements StorageAccess {
    
    private final SimpleTableManager tableManager = new SimpleTableManager();
    

    @Override
    public SimpleTableManager tables() {
        return tableManager;
    }

    @Override
    public NamedResourceStore<Constraint> constraints() {
        return new EmptyNamedResourceStore<>();
    }
    
}
