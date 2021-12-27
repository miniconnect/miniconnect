package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Sequence implements Iterable<BigInteger> {
    
    private final BigInteger until;
    

    public Sequence(long until) {
        this(BigInteger.valueOf(until));
    }
    
    public Sequence(BigInteger until) {
        if (until.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        
        this.until = until;
    }
    

    @Override
    public Iterator<BigInteger> iterator() {
        return new SequenceIterator();
    }
    
    
    private class SequenceIterator implements Iterator<BigInteger> {
        
        private BigInteger counter = BigInteger.ZERO;

        
        @Override
        public boolean hasNext() {
            return counter.compareTo(until) < 0;
        }

        @Override
        public BigInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            BigInteger result = counter;
            counter = counter.add(BigInteger.ONE);
            return result;
        }
        
    }

}
