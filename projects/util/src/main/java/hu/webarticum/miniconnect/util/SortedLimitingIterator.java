package hu.webarticum.miniconnect.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class SortedLimitingIterator<T> implements Iterator<T> {

    private final Iterator<T> resultIterator;
    

    public SortedLimitingIterator(Iterator<T> baseIterator, Comparator<T> comparator, long limit) {
        this(baseIterator, comparator, LargeInteger.of(limit));
    }
    
    public SortedLimitingIterator(Iterator<T> baseIterator, Comparator<T> comparator, LargeInteger limit) {
        this.resultIterator = collect(baseIterator, comparator, limit.intValueExact()).iterator();
    }
    
    private static <T> List<T> collect(Iterator<T> baseIterator, Comparator<T> comparator, int limit) {
        if (limit == 0) {
            return Collections.emptyList();
        }
        
        ArrayList<T> result = new ArrayList<>(limit * 2);
        while (baseIterator.hasNext()) {
            for (int i = 0; i < limit && baseIterator.hasNext(); i++) {
                result.add(baseIterator.next());
            }
            result.sort(comparator);
            for (int i = result.size() - 1; i >= limit; i--) {
                result.remove(i);
            }
        }
        result.trimToSize();
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
