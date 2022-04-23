package hu.webarticum.miniconnect.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainedIterator<T> implements Iterator<T> {
    
    private final Iterator<Iterator<T>> iteratorIterator;
    
    private Iterator<T> currentIterator;
    

    private ChainedIterator(Iterator<Iterator<T>> iteratorIterator) {
        this.iteratorIterator = iteratorIterator;
        
        fetchNextIterator();
    }

    @SafeVarargs
    public static <T> ChainedIterator<T> of(Iterator<T>... iterators) {
        return new ChainedIterator<>(Arrays.asList(iterators).iterator());
    }

    public static <T> ChainedIterator<T> allOf(Iterable<Iterator<T>> iterators) {
        return new ChainedIterator<>(iterators.iterator());
    }

    public static <T> ChainedIterator<T> over(Iterator<Iterator<T>> iteratorIterator) {
        return new ChainedIterator<>(iteratorIterator);
    }
    
    
    @Override
    public boolean hasNext() {
        while (currentIterator != null) {
            if (currentIterator.hasNext()) {
                return true;
            } else {
                fetchNextIterator();
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentIterator.next();
    }
    
    private void fetchNextIterator() {
        if (iteratorIterator.hasNext()) {
            currentIterator = iteratorIterator.next();
        } else {
            currentIterator = null;
        }
    }

}
