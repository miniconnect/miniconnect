package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class FilteringIteratorTest {

    @Test
    void testEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        FilteringIterator<Integer> filteringIterator =
                new FilteringIterator<>(baseIterator, v -> true);
        assertThat(iterableOf(filteringIterator)).isEmpty();
    }

    @Test
    void testFullyFiltered() {
        Iterator<Integer> baseIterator = Arrays.asList(2, 1, 4, 5, 5, 6).iterator();
        FilteringIterator<Integer> filteringIterator =
                new FilteringIterator<>(baseIterator, v -> false);
        assertThat(iterableOf(filteringIterator)).isEmpty();
    }

    @Test
    void testFilter() {
        Iterator<Integer> baseIterator = Arrays.asList(1, 1, 4, 3, 6, 5, 7, 4, 8, 7).iterator();
        FilteringIterator<Integer> filteringIterator =
                new FilteringIterator<>(baseIterator, v -> v % 2 == 0);
        assertThat(iterableOf(filteringIterator)).containsExactly(4, 6, 4, 8);
    }

    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
}
