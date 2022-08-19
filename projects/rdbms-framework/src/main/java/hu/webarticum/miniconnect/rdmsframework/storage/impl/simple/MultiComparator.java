package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class MultiComparator implements Comparator<ImmutableList<Object>> {
    
    private final ImmutableList<Comparator<Object>> comparators;
    
    
    @SuppressWarnings("unchecked")
    private MultiComparator(ImmutableList<? extends Comparator<?>> comparators) {
        this.comparators = (ImmutableList<Comparator<Object>>) comparators;
    }

    public static MultiComparator of(Comparator<?>... comparators) {
        return new MultiComparator(ImmutableList.of(comparators));
    }

    public static MultiComparator of(ImmutableList<? extends Comparator<?>> comparators) {
        return new MultiComparator(comparators);
    }
    
    public static MultiComparatorBuilder builder() {
        return new MultiComparatorBuilder();
    }

    
    @Override
    public int compare(ImmutableList<Object> values1, ImmutableList<Object> values2) {
        Iterator<Object> iteratorOfValues2 = values2.iterator();
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
    
    
    public static class MultiComparatorBuilder {
        
        private final List<Comparator<?>> comparators = new ArrayList<>();
        
        
        private MultiComparatorBuilder() {
        }
        
        
        public MultiComparatorBuilder add(Comparator<?> comparator) {
            comparators.add(comparator);
            return this;
        }

        public MultiComparatorBuilder add(
                Comparator<?> comparator, boolean nullable, boolean asc, boolean nullsLow) {
            Comparator<?> transformedComparator = comparator;
            if (nullable) {
                transformedComparator = nullsLow ?
                        Comparator.nullsFirst(comparator) :
                        Comparator.nullsLast(comparator);
            }
            if (!asc) {
                transformedComparator = transformedComparator.reversed();
            }
            comparators.add(transformedComparator);
            return this;
        }
        
        public MultiComparator build() {
            return new MultiComparator(ImmutableList.fromCollection(comparators));
        }

    }

}
