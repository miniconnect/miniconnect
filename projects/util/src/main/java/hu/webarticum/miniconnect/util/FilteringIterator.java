package hu.webarticum.miniconnect.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FilteringIterator<T> implements Iterator<T> {
    
    private final Iterator<T> baseIterator;
    
    private final Predicate<T> predicate;
    
    private T next;
    

    public FilteringIterator(Iterator<T> baseIterator, Predicate<T> predicate) {
        this.baseIterator = baseIterator;
        this.predicate = predicate;
        
        fetch();
    }
    
    
    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T result = next;
        fetch();
        return result;
    }
    
    private void fetch() {
        while (baseIterator.hasNext()) {
            T potentialNext = baseIterator.next();
            if (predicate.test(potentialNext)) {
                next = potentialNext;
                return;
            }
        }
        next = null;
    }

}
