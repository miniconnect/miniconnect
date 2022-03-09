package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RangeSelection implements TableSelection {
    
    private final BigInteger from;
    
    private final BigInteger until;
    
    private final OrderKey ascOrderKey;
    
    private final OrderKey descOrderKey;
    
    private final boolean ascOrder;

    public RangeSelection(long from, long until) {
        this(BigInteger.valueOf(from), BigInteger.valueOf(until));
    }
    
    public RangeSelection(BigInteger from, BigInteger until) {
        this(from, until, OrderKey.adHoc(), OrderKey.adHoc(), true);
    }
    
    public RangeSelection(
            BigInteger from,
            BigInteger until,
            OrderKey ascOrderKey,
            OrderKey descOrderKey,
            boolean ascOrder) {
        if (from.compareTo(until) > 0) {
            throw new IllegalArgumentException(String.format(
                    "From must be not greater than until (from: %d, until: %d)", from, until));
        }

        this.from = from;
        this.until = until;
        this.ascOrderKey = ascOrderKey;
        this.descOrderKey = descOrderKey;
        this.ascOrder = ascOrder;
    }
    

    @Override
    public Iterator<TableSelectionEntry> iterator() {
        return ascOrder ? new AscIterator() : new DescIterator();
    }

    @Override
    public OrderKey orderKey() {
        return ascOrder ? ascOrderKey : descOrderKey;
    }

    @Override
    public boolean containsRow(BigInteger rowIndex) {
        return (rowIndex.compareTo(from) >= 0 && rowIndex.compareTo(until) < 0);
    }

    @Override
    public RangeSelection reversed() {
        return new RangeSelection(from, until, ascOrderKey, descOrderKey, !ascOrder);
    }

    public boolean isAscOrder() {
        return ascOrder;
    }

    
    private class AscIterator implements Iterator<TableSelectionEntry> {

        private BigInteger next = from;
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(until) < 0;
        }

        @Override
        public TableSelectionEntry next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BigInteger orderIndex = next.subtract(from);
            TableSelectionEntry result = new TableSelectionEntry(ascOrderKey, next, orderIndex);
            next = next.add(BigInteger.ONE);
            return result;
        }
        
    }
    
    
    private class DescIterator implements Iterator<TableSelectionEntry> {

        private BigInteger next = until.subtract(BigInteger.ONE);
        
        
        @Override
        public boolean hasNext() {
            return next.compareTo(from) >= 0;
        }

        @Override
        public TableSelectionEntry next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BigInteger orderIndex = until.subtract(next).subtract(BigInteger.ONE);
            TableSelectionEntry result = new TableSelectionEntry(descOrderKey, next, orderIndex);
            next = next.subtract(BigInteger.ONE);
            return result;
        }
        
    }
    
    
}
