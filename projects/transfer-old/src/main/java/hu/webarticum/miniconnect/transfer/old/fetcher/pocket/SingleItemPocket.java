package hu.webarticum.miniconnect.transfer.old.fetcher.pocket;

import java.util.function.Predicate;

public class SingleItemPocket<T> implements Pocket<T, T> {
    
    private final Predicate<T> acceptor;
    
    
    private T item = null;
    
    
    public SingleItemPocket(Predicate<T> acceptor) {
        this.acceptor = acceptor;
    }
    

    @Override
    public AcceptStatus accept(T item) {
        if (!acceptor.test(item)) {
            return AcceptStatus.DECLINED;
        }
        
        synchronized (this) {
            if (this.item != null) {
                throw new IllegalStateException("Pocket is already completed");
            }
            this.item = item;
        }
        
        return AcceptStatus.COMPLETED;
    }

    @Override
    public boolean hasMore() {
        return isCompleted();
    }
    
    @Override
    public synchronized T get() {
        return item;
    }

    public synchronized boolean isCompleted() {
        return (item != null);
    }
    
}
