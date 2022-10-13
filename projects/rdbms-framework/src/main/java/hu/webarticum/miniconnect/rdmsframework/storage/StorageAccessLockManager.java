package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;

// TODO
// per table locking
// three lock level:
//   - query lock (now: shared)
//   - update prepare lock: when an update prepares, exclusive for update operationss
//       parallel select queries are allowed
//   - write lock: absolutely exclusive for the affected tables
//       this is the bottleneck when an update "commits"
//         or an update transaction starts writing to a specific table at the first time
public interface StorageAccessLockManager {

    public CheckableCloseable lockShared() throws InterruptedException;

    public CheckableCloseable lockExclusively() throws InterruptedException;

}
