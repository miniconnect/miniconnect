package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.LargeInteger;

class GroupingIteratorTest {

    @Test
    void testWhenEverythingIsEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        GroupingIterator<Integer, String> groupingIterator = new GroupingIterator<>(
                baseIterator,
                Comparator.naturalOrder(),
                (Iterator<Integer> i, LargeInteger p) -> Collections.emptyIterator());
        assertThat(iterableOf(groupingIterator)).isEmpty();
    }

    @Test
    void testGroupEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        GroupingIterator<Integer, String> groupingIterator = new GroupingIterator<>(
                baseIterator, Comparator.naturalOrder(), DecorateIntegersIterator::new);
        assertThat(iterableOf(groupingIterator)).isEmpty();
    }

    @Test
    void testEraseNonEmpty() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 3).iterator();
        GroupingIterator<Integer, String> groupingIterator = new GroupingIterator<>(
                baseIterator,
                Comparator.naturalOrder(),
                (Iterator<Integer> i, LargeInteger p) -> Collections.emptyIterator());
        assertThat(iterableOf(groupingIterator)).isEmpty();
    }

    @Test
    void testSimpleIsomorphism() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 3).iterator();
        GroupingIterator<Integer, String> groupingIterator = new GroupingIterator<>(
                baseIterator, Comparator.naturalOrder(), DecorateIntegersIterator::new);
        assertThat(iterableOf(groupingIterator)).containsExactly("[1]", "[2]", "[3]");
    }

    @Test
    void testGroupedIsomorphism() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 2, 3, 2, 4, 5, 5, 5, 6).iterator();
        GroupingIterator<Integer, String> groupingIterator = new GroupingIterator<>(
                baseIterator, Comparator.naturalOrder(), DecorateIntegersIterator::new);
        assertThat(iterableOf(groupingIterator)).containsExactly(
                "[1]", "[2]", "[2]", "[3]", "[2]", "[4]", "[5]", "[5]", "[5]", "[6]");
    }

    @Test
    void testCountGroup() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 2, 3, 2, 4, 5, 5, 5, 6).iterator();
        GroupingIterator<Integer, Integer> groupingIterator = new GroupingIterator<>(
                baseIterator, Comparator.naturalOrder(), this::countIterator);
        assertThat(iterableOf(groupingIterator)).containsExactly(1, 2, 1, 1, 1, 3, 1);
    }

    @Test
    void testCountGroupWithCustomComparator() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 2, 3, 2, 4, 5, 5, 5, 6).iterator();
        GroupingIterator<Integer, Integer> groupingIterator = new GroupingIterator<>(
                baseIterator, (a, b) -> Integer.valueOf(a / 3).compareTo(b / 3), this::countIterator);
        assertThat(iterableOf(groupingIterator)).containsExactly(3, 1, 1, 4, 1);
    }

    @Test
    void testListTransformer() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 2, 3, 2, 4, 5, 5, 5, 6).iterator();
        GroupingIterator<Integer, Integer> groupingIterator = new GroupingIterator<>(
                baseIterator, (a, b) -> Integer.valueOf(a / 3).compareTo(b / 3), this::reverseIntegerList);
        assertThat(iterableOf(groupingIterator)).containsExactly(2, 2, 1, 3, 2, 5, 5, 5, 4, 6);
    }

    @Test
    void testAbort() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 2, 2, 3, 2, 2, 4, 5, 5, 5, 6).iterator();
        GroupingIterator<Integer, Integer> groupingIterator = new GroupingIterator<>(
                baseIterator,
                Comparator.naturalOrder(),
                (Iterator<Integer> i, LargeInteger p) -> p.isLessThan(LargeInteger.of(5)) ? i : null);
        assertThat(iterableOf(groupingIterator)).containsExactly(1, 2, 2, 3, 2, 2);
    }
    
    private Iterator<Integer> countIterator(Iterator<?> iterator, LargeInteger position) {
        int result = 0;
        while (iterator.hasNext()) {
            iterator.next();
            result++;
        }
        return Arrays.asList(result).iterator();
    }
    
    private List<Integer> reverseIntegerList(List<Integer> originalList, LargeInteger position) {
        List<Integer> result = new ArrayList<>(originalList);
        Collections.reverse(result);
        return result;
    }
    
    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
    
    private static class DecorateIntegersIterator implements Iterator<String> {
        
        private Iterator<Integer> baseIterator;
        
        
        private DecorateIntegersIterator(Iterator<Integer> baseIterator, LargeInteger position) {
            this.baseIterator = baseIterator;
        }


        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            return "[" + baseIterator.next() + "]";
        }
        
    }

}
