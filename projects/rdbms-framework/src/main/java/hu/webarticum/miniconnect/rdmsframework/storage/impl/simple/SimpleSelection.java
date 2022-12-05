package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Iterator;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;

public class SimpleSelection implements TableSelection {
    
    private final Predicate<LargeInteger> containmentPredicate;
    
    private final Iterable<LargeInteger> rowIndexes;
    
    private final Iterable<LargeInteger> reverseRowIndexes;
    

    public SimpleSelection(ImmutableList<LargeInteger> rowIndexes) {
        this(
                rowIndexes::contains,
                rowIndexes,
                rowIndexes.reverseOrder());
    }

    public SimpleSelection(
            Predicate<LargeInteger> containmentPredicate,
            Iterable<LargeInteger> rowIndexes,
            Iterable<LargeInteger> reverseRowIndexes) {
        this.containmentPredicate = containmentPredicate;
        this.rowIndexes = rowIndexes;
        this.reverseRowIndexes = reverseRowIndexes;
    }
    

    @Override
    public Iterator<LargeInteger> iterator() {
        return rowIndexes.iterator();
    }

    @Override
    public boolean containsRow(LargeInteger rowIndex) {
        return containmentPredicate.test(rowIndex);
    }
    
    public SimpleSelection reversed() {
        return new SimpleSelection(
                containmentPredicate,
                reverseRowIndexes,
                rowIndexes);
    }
    
}
