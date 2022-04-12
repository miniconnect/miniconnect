package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

class ChainedIteratorTest {

    @Test
    void testNone() {
        ChainedIterator<Object> chainedIterator = ChainedIterator.of();
        assertThat(iterableOf(chainedIterator)).isEmpty();
    }

    @Test
    void testOne() {
        Iterator<Integer> iterator = Arrays.asList(3, 1, 4, 9, 2, 2, 3).iterator();
        ChainedIterator<Integer> chainedIterator = ChainedIterator.of(iterator);
        assertThat(iterableOf(chainedIterator)).containsExactly(3, 1, 4, 9, 2, 2, 3);
    }

    @Test
    void testTwo() {
        Iterator<Integer> iterator1 = Arrays.asList(9, 2, 3).iterator();
        Iterator<Integer> iterator2 = Arrays.asList(9, 4, 3, 5).iterator();
        ChainedIterator<Integer> chainedIterator = ChainedIterator.of(iterator1, iterator2);
        assertThat(iterableOf(chainedIterator)).containsExactly(9, 2, 3, 9, 4, 3, 5);
    }

    @Test
    void testThree() {
        Iterator<Integer> iterator1 = Arrays.asList(7, 3, 5, 6).iterator();
        Iterator<Integer> iterator2 = Arrays.asList(5, 2).iterator();
        Iterator<Integer> iterator3 = Arrays.asList(7, 1, 5, 4).iterator();
        ChainedIterator<Integer> chainedIterator =
                ChainedIterator.of(iterator1, iterator2, iterator3);
        assertThat(iterableOf(chainedIterator)).containsExactly(7, 3, 5, 6, 5, 2, 7, 1, 5, 4);
    }

    @Test
    void testAllOf() {
        List<Iterator<Integer>> iterators = Arrays.asList(
                Arrays.asList(1, 3, 4).iterator(),
                Arrays.asList(2, 3, 1).iterator(),
                Arrays.asList(0, 5).iterator());
        ChainedIterator<Integer> chainedIterator = ChainedIterator.allOf(iterators);
        assertThat(iterableOf(chainedIterator)).containsExactly(1, 3, 4, 2, 3, 1, 0, 5);
    }

    @Test
    void testOver() {
        Iterator<Iterator<Integer>> iteratorIterator = Arrays.asList(
                Arrays.asList(9, 2, 3).iterator(),
                Arrays.asList(6).iterator(),
                Arrays.asList(new Integer[] {}).iterator(),
                Arrays.asList(2, 3).iterator()).iterator();
        ChainedIterator<Integer> chainedIterator = ChainedIterator.over(iteratorIterator);
        assertThat(iterableOf(chainedIterator)).containsExactly(9, 2, 3, 6, 2, 3);
    }
    
    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
}
