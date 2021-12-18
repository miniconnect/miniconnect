package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CountDown implements Iterable<BigInteger> {
    
    private final BigInteger initial;
    

    public CountDown(long initial) {
        this(BigInteger.valueOf(initial));
    }
    
    public CountDown(BigInteger initial) {
        if (initial.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        
        this.initial = initial;
    }
    

    @Override
    public Iterator<BigInteger> iterator() {
        return new CountDownIterator();
    }
    
    
    private class CountDownIterator implements Iterator<BigInteger> {
        
        private BigInteger counter = initial.subtract(BigInteger.ONE);

        
        @Override
        public boolean hasNext() {
            return counter.compareTo(BigInteger.ZERO) >= 0;
        }

        @Override
        public BigInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            BigInteger result = counter;
            counter = counter.subtract(BigInteger.ONE);
            return result;
        }
        
    }

}
