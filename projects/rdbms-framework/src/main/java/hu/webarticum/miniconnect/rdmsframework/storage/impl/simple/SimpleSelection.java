package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;

public class SimpleSelection implements TableSelection {
    
    private final Predicate<BigInteger> containmentPredicate;
    
    private final Iterable<BigInteger> rowIndexes;
    
    private final Iterable<BigInteger> reverseRowIndexes;
    

    public SimpleSelection(ImmutableList<BigInteger> rowIndexes) {
        this(
                rowIndexes::contains,
                rowIndexes,
                rowIndexes.reverseOrder());
    }

    public SimpleSelection(
            BigInteger tableSize,
            ImmutableList<BigInteger> rowIndexes) {
        this(
                i -> i.compareTo(tableSize) < 0,
                rowIndexes,
                rowIndexes.reverseOrder());
    }
    
    public SimpleSelection(
            Predicate<BigInteger> containmentPredicate,
            Iterable<BigInteger> rowIndexes,
            Iterable<BigInteger> reverseRowIndexes) {
        this.containmentPredicate = containmentPredicate;
        this.rowIndexes = rowIndexes;
        this.reverseRowIndexes = reverseRowIndexes;
    }
    

    @Override
    public Iterator<BigInteger> iterator() {
        return rowIndexes.iterator();
    }

    @Override
    public boolean containsRow(BigInteger rowIndex) {
        return containmentPredicate.test(rowIndex);
    }
    
    @Override
    public SimpleSelection reversed() {
        return new SimpleSelection(
                containmentPredicate,
                reverseRowIndexes,
                rowIndexes);
    }
    
}
