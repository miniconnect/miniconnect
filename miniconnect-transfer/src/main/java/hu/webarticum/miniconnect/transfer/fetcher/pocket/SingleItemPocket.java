package hu.webarticum.miniconnect.transfer.fetcher.pocket;

import java.util.function.Predicate;

public class SingleItemPocket<T> implements Pocket<T> {
    
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

    public synchronized boolean isCompleted() {
        return (item != null);
    }

    public synchronized T get() {
        return item;
    }

}
