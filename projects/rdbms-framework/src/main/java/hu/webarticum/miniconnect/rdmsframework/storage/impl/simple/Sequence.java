package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Iterator;
import java.util.NoSuchElementException;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class Sequence implements Iterable<LargeInteger> {
    
    private final LargeInteger until;
    

    public Sequence(long until) {
        this(LargeInteger.of(until));
    }
    
    public Sequence(LargeInteger until) {
        if (until.isNegative()) {
            throw new IllegalArgumentException();
        }
        
        this.until = until;
    }
    

    @Override
    public Iterator<LargeInteger> iterator() {
        return new SequenceIterator();
    }
    
    
    private class SequenceIterator implements Iterator<LargeInteger> {
        
        private LargeInteger counter = LargeInteger.ZERO;

        
        @Override
        public boolean hasNext() {
            return counter.compareTo(until) < 0;
        }

        @Override
        public LargeInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            LargeInteger result = counter;
            counter = counter.add(LargeInteger.ONE);
            return result;
        }
        
    }

}
