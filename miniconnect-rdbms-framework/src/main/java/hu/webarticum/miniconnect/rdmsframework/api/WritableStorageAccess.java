package hu.webarticum.miniconnect.rdmsframework.api;

// TODO: AdminStorageAccess? (for changing schema)

public interface WritableStorageAccess extends StorageAccess {

    public void applyPatch(StoragePatch patch);
    
}
