package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelectionEntry;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class SimpleSelection implements TableSelection {
    
    private final Predicate<BigInteger> containmentPredicate;
    
    private final Object orderKey;
    
    private final Object reverseOrderKey;
    
    private final Iterable<BigInteger> rowIndexes;
    
    private final Iterable<BigInteger> reverseRowIndexes;
    
    private final Iterable<BigInteger> orderIndexes;
    
    private final Iterable<BigInteger> reverseOrderIndexes;
    

    public SimpleSelection(ImmutableList<BigInteger> rowIndexes) {
        this(
                rowIndexes::contains,
                new Object(),
                new Object(),
                rowIndexes,
                rowIndexes.reverseOrder(),
                new Sequence(BigInteger.valueOf(rowIndexes.size())),
                new Sequence(BigInteger.valueOf(rowIndexes.size())));
    }

    public SimpleSelection(
            BigInteger tableSize,
            Object orderKey,
            Object reverseOrderKey,
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
            Object orderKey,
            Object reverseOrderKey,
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
    public Object orderKey() {
        return orderKey;
    }

    @Override
    public boolean containsRow(BigInteger rowIndex) {
        return containmentPredicate.test(rowIndex);
    }
    
    @Override
    public TableSelection reverse() {
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
