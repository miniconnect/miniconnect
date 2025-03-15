package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class LimitingIteratorTest {

    @Test
    void testClearEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 0);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testClear() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4).iterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 0);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testLimitEmpty() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 17);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testLimitEmptyToNegative() {
        Iterator<Integer> baseIterator = Collections.emptyIterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, -8);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }

    @Test
    void testLimit() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4, 8, 3, 12, -1).iterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 4);
        assertThat(iterableOf(limitingIterator)).containsExactly(3, 2, 3, 4);
    }

    @Test
    void testLimitToSameSize() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4, 8, 3, 12, -1).iterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 8);
        assertThat(iterableOf(limitingIterator)).containsExactly(3, 2, 3, 4, 8, 3, 12, -1);
    }

    @Test
    void testLimitToLargerSize() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4, 8, 3, 12, -1).iterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, 137);
        assertThat(iterableOf(limitingIterator)).containsExactly(3, 2, 3, 4, 8, 3, 12, -1);
    }

    @Test
    void testLimitToNegative() {
        Iterator<Integer> baseIterator = Arrays.asList(3, 2, 3, 4, 8, 3, 12, -1).iterator();
        LimitingIterator<Integer> limitingIterator = new LimitingIterator<>(baseIterator, -5);
        assertThat(iterableOf(limitingIterator)).isEmpty();
    }
    
    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
}
