package hu.webarticum.miniconnect.server.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Queue variant for aligning ordering anomalies
 * 
 * It must be possible to decide on each incoming item,
 * that it is the next (possibly the first) element or not.
 * If an item is detected as next,
 * then it will be appended to the queue;
 * if not, then it will be stored to check again later.
 * 
 * @param <T> any type
 */
public class StrictlySortedQueue<T> {
    
    private final NextChecker<T> nextChecker;
    
    private final LinkedList<T> unorderedItems = new LinkedList<>();
    
    private final BlockingQueue<T> queue = new LinkedBlockingDeque<>();
    
    private T previous = null;
    

    public StrictlySortedQueue(NextChecker<T> nextChecker) {
        this.nextChecker = nextChecker;
    }
    
    
    public synchronized void add(T item) {
        if (!nextChecker.isNext(previous, item)) {
            unorderedItems.add(item);
            return;
        }
        
        previous = item;
        queue.add(item);
        
        boolean doIterate = !unorderedItems.isEmpty();
        while (doIterate) {
            doIterate = false;
            Iterator<T> unorderedIterator = unorderedItems.iterator();
            while (unorderedIterator.hasNext()) {
                T unorderedItem = unorderedIterator.next();
                if (nextChecker.isNext(previous, unorderedItem)) {
                    unorderedIterator.remove();
                    previous = unorderedItem;
                    queue.add(unorderedItem);
                    
                    doIterate = true;
                    break;
                }
            }
        }
    }
    
    public boolean available() {
        return !queue.isEmpty();
    }
    
    public T take(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        T item = queue.poll(timeout, unit);
        if (item == null) {
            throw new TimeoutException();
        }
        
        return item;
    }

    public T take() throws InterruptedException {
        return queue.take();
    }
    
    
    @FunctionalInterface
    public interface NextChecker<T> {
        
        public boolean isNext(T previous, T itemToCheck);
        
    }
    
}
