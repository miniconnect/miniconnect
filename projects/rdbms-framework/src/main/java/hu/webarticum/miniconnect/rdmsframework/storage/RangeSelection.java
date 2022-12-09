package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class RangeSelection implements TableSelection {
    
    private final LargeInteger from;
    
    private final LargeInteger until;
    
    private final boolean ascOrder;

    public RangeSelection(long from, long until) {
        this(LargeInteger.of(from), LargeInteger.of(until));
    }
    
    public RangeSelection(LargeInteger from, LargeInteger until) {
        this(from, until, true);
    }
    
    public RangeSelection(
            LargeInteger from,
            LargeInteger until,
            boolean ascOrder) {
        if (from.compareTo(until) > 0) {
            throw new IllegalArgumentException(String.format(
                    "From must be not greater than until (from: %d, until: %d)",
                    from.bigIntegerValue(),
                    until.bigIntegerValue()));
        }

        this.from = from;
        this.until = until;
        this.ascOrder = ascOrder;
    }
    

    @Override
    public Iterator<LargeInteger> iterator() {
        return ascOrder ? new AscIterator() : new DescIterator();
    }

    @Override
    public boolean containsRow(LargeInteger rowIndex) {
        return (rowIndex.compareTo(from) >= 0 && rowIndex.compareTo(until) < 0);
    }

    public RangeSelection reversed() {
        return new RangeSelection(from, until, !ascOrder);
    }

    public boolean isAscOrder() {
        return ascOrder;
    }

    
    private class AscIterator implements Iterator<LargeInteger> {

        private LargeInteger next = from;
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(until) < 0;
        }

        @Override
        public LargeInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            LargeInteger result = next;
            next = next.add(LargeInteger.ONE);
            return result;
        }
        
    }
    
    
    private class DescIterator implements Iterator<LargeInteger> {

        private LargeInteger next = until.subtract(LargeInteger.ONE);
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(from) >= 0;
        }

        @Override
        public LargeInteger next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            LargeInteger result = next;
            next = next.subtract(LargeInteger.ONE);
            return result;
        }
        
    }
    
    
}
