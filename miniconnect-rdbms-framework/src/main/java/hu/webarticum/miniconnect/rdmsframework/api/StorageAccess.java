package hu.webarticum.miniconnect.rdmsframework.api;

public interface StorageAccess {
    
    public boolean isWritable();

    public void applyPatch(StoragePatch patch);
    
    public NamedResourceStore<Table> tables();

    public NamedResourceStore<Constraint> constraints();
    
    // TODO: views, triggers, procedures, sequences etc.
    
}
