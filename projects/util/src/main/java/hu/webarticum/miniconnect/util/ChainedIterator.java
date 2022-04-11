package hu.webarticum.miniconnect.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class ChainedIterator<T> implements Iterator<T> {
    
    private final LinkedList<Iterator<T>> iterators = new LinkedList<>();
    

    @SafeVarargs
    public ChainedIterator(Iterator<T>... iterators) {
        this(Arrays.asList(iterators));
    }

    public ChainedIterator(Collection<Iterator<T>> iterators) {
        this.iterators.addAll(iterators);
    }
    
    
    @Override
    public boolean hasNext() {
        while (!iterators.isEmpty()) {
            if (iterators.getFirst().hasNext()) {
                return true;
            } else {
                iterators.removeFirst();
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return iterators.getFirst().next();
    }

}
