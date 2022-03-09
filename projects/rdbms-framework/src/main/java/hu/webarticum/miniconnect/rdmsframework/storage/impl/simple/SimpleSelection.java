package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.OrderKey;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelectionEntry;

public class SimpleSelection implements TableSelection {
    
    private final Predicate<BigInteger> containmentPredicate;
    
    private final OrderKey orderKey;
    
    private final OrderKey reverseOrderKey;
    
    private final Iterable<BigInteger> rowIndexes;
    
    private final Iterable<BigInteger> reverseRowIndexes;
    
    private final Iterable<BigInteger> orderIndexes;
    
    private final Iterable<BigInteger> reverseOrderIndexes;
    

    public SimpleSelection(ImmutableList<BigInteger> rowIndexes) {
        this(
                rowIndexes::contains,
                OrderKey.adHoc(),
                OrderKey.adHoc(),
                rowIndexes,
                rowIndexes.reverseOrder(),
                new Sequence(BigInteger.valueOf(rowIndexes.size())),
                new Sequence(BigInteger.valueOf(rowIndexes.size())));
    }

    public SimpleSelection(
            BigInteger tableSize,
            OrderKey orderKey,
            OrderKey reverseOrderKey,
            ImmutableList<BigInteger> rowIndexes,
            ImmutableList<BigInteger> orderIndexes) {
        this(
                rowIndexes::contains,
                orderKey,
                reverseOrderKey,
                rowIndexes,
                rowIndexes.reverseOrder(),
                orderIndexes,
                orderIndexes.map(tableSize.subtract(BigInteger.ONE)::subtract).reverseOrder());
    }
    
    public SimpleSelection(
            Predicate<BigInteger> containmentPredicate,
            OrderKey orderKey,
            OrderKey reverseOrderKey,
            Iterable<BigInteger> rowIndexes,
            Iterable<BigInteger> reverseRowIndexes,
            Iterable<BigInteger> orderIndexes,
            Iterable<BigInteger> reverseOrderIndexes) {
        this.orderKey = orderKey;
        this.reverseOrderKey = reverseOrderKey;
        this.containmentPredicate = containmentPredicate;
        this.rowIndexes = rowIndexes;
        this.reverseRowIndexes = reverseRowIndexes;
        this.orderIndexes = orderIndexes;
        this.reverseOrderIndexes = reverseOrderIndexes;
    }
    

    @Override
    public Iterator<TableSelectionEntry> iterator() {
        return new SimpleSelectionIterator();
    }

    @Override
    public OrderKey orderKey() {
        return orderKey;
    }

    @Override
    public boolean containsRow(BigInteger rowIndex) {
        return containmentPredicate.test(rowIndex);
    }
    
    @Override
    public SimpleSelection reversed() {
        return new SimpleSelection(
                containmentPredicate,
                reverseOrderKey,
                orderKey,
                reverseRowIndexes,
                rowIndexes,
                reverseOrderIndexes,
                orderIndexes);
    }
    
    
    private class SimpleSelectionIterator implements Iterator<TableSelectionEntry> {

        private Iterator<BigInteger> rowIndexIterator = rowIndexes.iterator();

        private Iterator<BigInteger> orderIndexIterator = orderIndexes.iterator();
        
        
        @Override
        public boolean hasNext() {
            return rowIndexIterator.hasNext();
        }

        @Override
        public TableSelectionEntry next() {
            BigInteger rowIndex = rowIndexIterator.next();
            BigInteger orderIndex = orderIndexIterator.next();
            return new TableSelectionEntry(orderKey, rowIndex, orderIndex);
        }
        
    }
    
}
