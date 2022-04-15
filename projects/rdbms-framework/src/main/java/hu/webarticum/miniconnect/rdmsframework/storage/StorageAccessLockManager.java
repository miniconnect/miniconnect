package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;

public interface StorageAccessLockManager {

    public CheckableCloseable lockShared() throws InterruptedException;

    public CheckableCloseable lockExclusively() throws InterruptedException;

}
