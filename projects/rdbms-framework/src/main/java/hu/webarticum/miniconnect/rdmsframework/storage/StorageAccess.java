package hu.webarticum.miniconnect.rdmsframework.storage;

public interface StorageAccess {
    
    public NamedResourceStore<Schema> schemas();

    public NamedResourceStore<Constraint> constraints();
    
    // TODO: views, triggers, procedures, sequences etc.
    
}
