package hu.webarticum.miniconnect.rdmsframework.storage.impl.selection;

import java.math.BigInteger;
import java.util.Iterator;

import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelectionEntry;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class SimpleSelection implements TableSelection {
    
    private final Object orderKey;
    
    private final ImmutableList<BigInteger> rowIndexes;
    
    private final Iterable<BigInteger> orderIndexes;
    

    public SimpleSelection(
            Object orderKey,
            ImmutableList<BigInteger> rowIndexes,
            Iterable<BigInteger> orderIndexes) {
        this.orderKey = orderKey;
        this.rowIndexes = rowIndexes;
        this.orderIndexes = orderIndexes;
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
        return rowIndexes.contains(rowIndex);
    }
    
    @Override
    public TableSelection reverse() {
        
        
        // TODO
        // FIXME: how to reverse rowIndexes?
        return null;
        
        
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
