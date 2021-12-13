package hu.webarticum.miniconnect.rdmsframework.storage;

public interface StorageAccess {
    
    public NamedResourceStore<Table> tables();

    public NamedResourceStore<Constraint> constraints();
    
    // TODO: views, triggers, procedures, sequences etc.
    
}
