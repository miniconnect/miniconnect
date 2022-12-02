package hu.webarticum.miniconnect.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LimitingIterator<T> implements Iterator<T> {
    
    private final Iterator<T> baseIterator;
    
    private BigInteger rest;
    

    public LimitingIterator(Iterator<T> baseIterator, long limit) {
        this(baseIterator, BigInteger.valueOf(limit));
    }
    
    public LimitingIterator(Iterator<T> baseIterator, BigInteger limit) {
        this.baseIterator = baseIterator;
        this.rest = limit;
    }
    
    
    @Override
    public boolean hasNext() {
        return rest.compareTo(BigInteger.ZERO) > 0 && baseIterator.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        rest = rest.subtract(BigInteger.ONE);
        return baseIterator.next();
    }
    
}
