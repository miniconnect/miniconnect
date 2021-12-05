package hu.webarticum.miniconnect.rdmsframework.api;

public interface Transaction extends StorageAccess {

    public void commit();
    
    public void rollback();
    
}
