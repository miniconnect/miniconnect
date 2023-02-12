package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class SortedLimitingIteratorTest {

    @Test
    void testClearEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        SortedLimitingIterator<Integer> limitingIterator = new SortedLimitingIterator<>(baseIterator, comparator(), 0);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testClear() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4).iterator();
        SortedLimitingIterator<Integer> limitingIterator = new SortedLimitingIterator<>(baseIterator, comparator(), 0);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testLimit() {
        Iterator<Integer> baseIterator = Arrays.asList(2, 4, 12, 3, 2, 12, 4, 1, 7, 3, 3).iterator();
        SortedLimitingIterator<Integer> limitingIterator = new SortedLimitingIterator<>(baseIterator, comparator(), 5);
        assertThat(iterableOf(limitingIterator)).containsExactly(1, 2, 2, 3, 3);
    }

    @Test
    void testLargerLimit() {
        Iterator<Integer> baseIterator = Arrays.asList(7, 5, 3, 4, 2, 5).iterator();
        SortedLimitingIterator<Integer> limitingIterator = new SortedLimitingIterator<>(baseIterator, comparator(), 10);
        assertThat(iterableOf(limitingIterator)).containsExactly(2, 3, 4, 5, 5, 7);
    }

    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
    private Comparator<Integer> comparator() {
        return (a, b) -> Integer.compare(a, b);
    }
    
}
