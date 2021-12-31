package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.rdmsframework.execution.fake.EmptyNamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Constraint;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;

// TODO
public class SimpleStorageAccess implements StorageAccess {

    @Override
    public NamedResourceStore<Table> tables() {
        return new EmptyNamedResourceStore<>();
    }

    @Override
    public NamedResourceStore<Constraint> constraints() {
        return new EmptyNamedResourceStore<>();
    }
    
    
    

}
