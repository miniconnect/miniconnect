package hu.webarticum.miniconnect.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortingIterator<T> implements Iterator<T> {

    private final Iterator<T> resultIterator;
    

    public SortingIterator(Iterator<T> baseIterator, Comparator<T> comparator) {
        this.resultIterator = collect(baseIterator, comparator).iterator();
    }
    
    private static <T> List<T> collect(Iterator<T> baseIterator, Comparator<T> comparator) {
        List<T> result = new ArrayList<>();
        while (baseIterator.hasNext()) {
            result.add(baseIterator.next());
        }
        result.sort(comparator);
        return result;
    }
    
    
    @Override
    public boolean hasNext() {
        return resultIterator.hasNext();
    }
    
    @Override
    public T next() {
        return resultIterator.next();
    }
    
}
