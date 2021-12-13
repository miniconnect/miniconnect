package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public class MultiComparator implements Comparator<ImmutableList<Object>> {
    
    private final ImmutableList<Comparator<Object>> comparators;
    
    
    public MultiComparator(Comparator<?>... comparators) {
        this(ImmutableList.of(comparators));
    }

    public MultiComparator(Collection<? extends Comparator<?>> comparators) {
        this(new ImmutableList<>(comparators));
    }

    @SuppressWarnings("unchecked")
    public MultiComparator(ImmutableList<? extends Comparator<?>> comparators) {
        this.comparators = (ImmutableList<Comparator<Object>>) comparators;
    }

    
    @Override
    public int compare(ImmutableList<Object> values1, ImmutableList<Object> values2) {
        Iterator<Comparator<Object>> iteratorOfValues2 = comparators.iterator();
        Iterator<Comparator<Object>> iteratorOfComparators = comparators.iterator();
        for (Object value1 : values1) {
            Object value2 = iteratorOfValues2.next();
            Comparator<Object> comparator = iteratorOfComparators.next();
            int cmp = comparator.compare(value1, value2);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

}
