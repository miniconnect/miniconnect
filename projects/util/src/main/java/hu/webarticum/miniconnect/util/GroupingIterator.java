package hu.webarticum.miniconnect.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class GroupingIterator<T, U> implements Iterator<U> {

    private final Iterator<T> baseIterator;
    
    private final Comparator<T> groupComparator;
    
    private final IteratorRowTransformator<T, U> rowTransformator;
    
    private Iterator<U> nextIterator = null;
    
    private T nextElement = null;
    

    public GroupingIterator(
            Iterator<T> baseIterator,
            Comparator<T> groupComparator, 
            ListRowTransformator<T, U> rowTransformator) {
        this(baseIterator, groupComparator, toIteratorRowTransformator(rowTransformator));
    }
    
    private static <T, U> IteratorRowTransformator<T, U> toIteratorRowTransformator(
            ListRowTransformator<T, U> listRowTransformator) {
        return t -> listRowTransformator.apply(collectIterator(t)).iterator();
    }
    
    private static <T> List<T> collectIterator(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        iterator.forEachRemaining(result::add);
        return result;
    }
    
    public GroupingIterator(
            Iterator<T> baseIterator,
            Comparator<T> groupComparator, 
            IteratorRowTransformator<T, U> rowTransformator) {
        this.baseIterator = baseIterator;
        this.groupComparator = groupComparator;
        this.rowTransformator = rowTransformator;
    }

    
    @Override
    public boolean hasNext() {
        ensureNextIterator();
        return (nextIterator != null && nextIterator.hasNext());
    }

    @Override
    public U next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return nextIterator.next();
    }
    
    private void ensureNextIterator() {
        if (nextIterator != null && nextIterator.hasNext()) {
            return;
        }
        
        loadNextIterator();
    }
    
    private void loadNextIterator() {
        ensureNextElement();
        if (nextElement == null) {
            nextIterator = null;
            return;
        }
        
        Iterator<T> nextInnerIterator = new InnerGroupIterator();
        nextIterator = rowTransformator.apply(nextInnerIterator);
    }

    private void ensureNextElement() {
        if (nextElement == null && baseIterator.hasNext()) {
            nextElement = baseIterator.next();
        }
    }
    
    
    @FunctionalInterface
    public interface IteratorRowTransformator<T, U> extends Function<Iterator<T>, Iterator<U>> {
    }
    

    @FunctionalInterface
    public interface ListRowTransformator<T, U> extends Function<List<T>, List<U>> {
    }
    
    
    private class InnerGroupIterator implements Iterator<T> {

        private final T referenceElement;
        
        
        private InnerGroupIterator() {
            referenceElement = nextElement;
        }

        
        @Override
        public boolean hasNext() {
            ensureNextElement();
            if (nextElement == null) {
                return false;
            }
            
            return
                    referenceElement == nextElement ||
                    groupComparator.compare(referenceElement, nextElement) == 0;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            T result = nextElement;
            nextElement = null;
            return result;
        }

    }
    
}
