package hu.webarticum.miniconnect.rdmsframework.database;

import hu.webarticum.miniconnect.rdmsframework.table.Table;

public interface StorageAccess {
    
    public NamedResourceStore<Table> tables();

    public NamedResourceStore<Constraint> constraints();
    
    // TODO: views, triggers, procedures, sequences etc.
    
}
