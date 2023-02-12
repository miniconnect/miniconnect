package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class SortingIteratorTest {

    @Test
    void testEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        SortingIterator<Integer> sortingIterator = new SortingIterator<>(baseIterator, comparator());
        assertThat(iterableOf(sortingIterator)).isEmpty();
    }

    @Test
    void testSort() {
        Iterator<Integer> baseIterator = Arrays.asList(2, 4, 12, 3, 2, 12, 4, 1, 7, 3, 3).iterator();
        SortingIterator<Integer> sortingIterator = new SortingIterator<>(baseIterator, comparator());
        assertThat(iterableOf(sortingIterator)).containsExactly(1, 2, 2, 3, 3, 3, 4, 4, 7, 12, 12);
    }

    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
    private Comparator<Integer> comparator() {
        return (a, b) -> Integer.compare(a, b);
    }
    
}
