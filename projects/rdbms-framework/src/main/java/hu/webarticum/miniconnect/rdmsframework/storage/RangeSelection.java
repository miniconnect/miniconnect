package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RangeSelection implements TableSelection {
    
    private final BigInteger from;
    
    private final BigInteger until;
    
    private final boolean ascOrder;

    public RangeSelection(long from, long until) {
        this(BigInteger.valueOf(from), BigInteger.valueOf(until));
    }
    
    public RangeSelection(BigInteger from, BigInteger until) {
        this(from, until, true);
    }
    
    public RangeSelection(
            BigInteger from,
            BigInteger until,
            boolean ascOrder) {
        if (from.compareTo(until) > 0) {
            throw new IllegalArgumentException(String.format(
                    "From must be not greater than until (from: %d, until: %d)", from, until));
        }

        this.from = from;
        this.until = until;
        this.ascOrder = ascOrder;
    }
    

    @Override
    public Iterator<BigInteger> iterator() {
        return ascOrder ? new AscIterator() : new DescIterator();
    }

    @Override
    public boolean containsRow(BigInteger rowIndex) {
        return (rowIndex.compareTo(from) >= 0 && rowIndex.compareTo(until) < 0);
    }

    @Override
    public RangeSelection reversed() {
        return new RangeSelection(from, until, !ascOrder);
    }

    public boolean isAscOrder() {
        return ascOrder;
    }

    
    private class AscIterator implements Iterator<BigInteger> {

        private BigInteger next = from;
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(until) < 0;
        }

        @Override
        public BigInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BigInteger result = next;
            next = next.add(BigInteger.ONE);
            return result;
        }
        
    }
    
    
    private class DescIterator implements Iterator<BigInteger> {

        private BigInteger next = until.subtract(BigInteger.ONE);
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(from) >= 0;
        }

        @Override
        public BigInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BigInteger result = next;
            next = next.subtract(BigInteger.ONE);
            return result;
        }
        
    }
    
    
}
