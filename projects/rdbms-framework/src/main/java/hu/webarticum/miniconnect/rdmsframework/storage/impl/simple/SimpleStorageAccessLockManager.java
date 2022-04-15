package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccessLockManager;

public class SimpleStorageAccessLockManager implements StorageAccessLockManager {
    
    private enum LockStatus { WAITING, ACTIVE, CLOSED }
    
    
    private final Set<ManagedLock> waitingSharedLocks = new HashSet<>();
    
    private final Set<ManagedLock> activeSharedLocks = new HashSet<>();
    
    private final Queue<ManagedLock> exclusiveLockQueue = new LinkedList<>();
    
    private ManagedLock activeExclusiveLock = null;
    

    @Override
    public CheckableCloseable lockShared() throws InterruptedException {
        ManagedLock resultLock = new ManagedLock();
        boolean mustWait = true;
        synchronized (this) {
            if (exclusiveLockQueue.isEmpty() && activeExclusiveLock == null) {
                activeSharedLocks.add(resultLock);
                resultLock.status = LockStatus.ACTIVE;
                mustWait = false;
            } else {
                waitingSharedLocks.add(resultLock);
            }
        }
        if (mustWait) {
            try {
                resultLock.waitForStart();
            } catch (InterruptedException e) {
                synchronized (this) {
                    waitingSharedLocks.remove(resultLock);
                    activeSharedLocks.remove(resultLock);
                }
                throw e;
            }
        }
        return resultLock;
    }

    @Override
    public CheckableCloseable lockExclusively() throws InterruptedException {
        ManagedLock resultLock = new ManagedLock();
        boolean mustWait = true;
        synchronized (this) {
            if (activeSharedLocks.isEmpty() && activeExclusiveLock == null) {
                activeExclusiveLock = resultLock;
                resultLock.status = LockStatus.ACTIVE;
                mustWait = false;
            } else {
                exclusiveLockQueue.add(resultLock);
            }
        }
        if (mustWait) {
            try {
                resultLock.waitForStart();
            } catch (InterruptedException e) {
                synchronized (this) {
                    exclusiveLockQueue.remove(resultLock);
                    if (activeExclusiveLock == resultLock) {
                        activeExclusiveLock = null;
                    }
                }
                throw e;
            }
        }
        return resultLock;
    }

    
    private class ManagedLock implements CheckableCloseable {

        private volatile LockStatus status = LockStatus.WAITING;
        
        
        private synchronized void waitForStart() throws InterruptedException {
            while (status == LockStatus.WAITING) {
                wait();
            }
        }
        
        @Override
        public void close() {
            if (status == LockStatus.CLOSED) {
                return;
            }
            
            synchronized (this) {
                status = LockStatus.CLOSED;
            }
            
            List<ManagedLock> locksToStart = new ArrayList<>(0);
            
            synchronized (SimpleStorageAccessLockManager.this) {
                boolean mustHandleExclusiveQueue = false;
                if (activeExclusiveLock == this) {
                    activeExclusiveLock = null;
                    if (waitingSharedLocks.isEmpty()) {
                        mustHandleExclusiveQueue = true;
                    } else {
                        activeSharedLocks.addAll(waitingSharedLocks);
                        locksToStart.addAll(waitingSharedLocks);
                        waitingSharedLocks.clear();
                    }
                } else {
                    activeSharedLocks.remove(this);
                    if (activeSharedLocks.isEmpty()) {
                        mustHandleExclusiveQueue = true;
                    }
                }
                if (mustHandleExclusiveQueue) {
                    ManagedLock nextExclusiveLock = exclusiveLockQueue.poll();
                    if (nextExclusiveLock != null) {
                        activeExclusiveLock = nextExclusiveLock;
                        locksToStart.add(nextExclusiveLock);
                    }
                }
            }
            
            for (ManagedLock lockToStart : locksToStart) {
                synchronized (lockToStart) {
                    lockToStart.status = LockStatus.ACTIVE;
                    lockToStart.notifyAll();
                }
            }
        }

        @Override
        public boolean isClosed() {
            return status == LockStatus.CLOSED;
        }
        
    }

}
