package hu.webarticum.miniconnect.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class LimitingIterator<T> implements Iterator<T> {
    
    private final Iterator<T> baseIterator;
    
    private LargeInteger rest;
    

    public LimitingIterator(Iterator<T> baseIterator, long limit) {
        this(baseIterator, LargeInteger.of(limit));
    }
    
    public LimitingIterator(Iterator<T> baseIterator, LargeInteger limit) {
        this.baseIterator = baseIterator;
        this.rest = limit;
    }
    
    
    @Override
    public boolean hasNext() {
        return rest.compareTo(LargeInteger.ZERO) > 0 && baseIterator.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        rest = rest.subtract(LargeInteger.ONE);
        return baseIterator.next();
    }
    
}
