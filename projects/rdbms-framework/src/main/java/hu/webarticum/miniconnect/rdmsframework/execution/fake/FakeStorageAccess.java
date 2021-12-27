package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import hu.webarticum.miniconnect.rdmsframework.storage.Constraint;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;

public class FakeStorageAccess implements StorageAccess {

    @Override
    public NamedResourceStore<Table> tables() {
        return new EmptyNamedResourceStore<>();
    }

    @Override
    public NamedResourceStore<Constraint> constraints() {
        return new EmptyNamedResourceStore<>();
    }
    
    
    

}
