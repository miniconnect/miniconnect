package hu.webarticum.miniconnect.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class ChainedIteratorTest {

    @Test
    void testNone() {
        ChainedIterator<Object> chainedIterator = new ChainedIterator<>();
        assertThat(iterableOf(chainedIterator)).isEmpty();
    }

    @Test
    void testOne() {
        Iterator<Integer> iterator = Arrays.asList(3, 1, 4, 9, 2, 2, 3).iterator();
        ChainedIterator<Integer> chainedIterator = new ChainedIterator<>(iterator);
        assertThat(iterableOf(chainedIterator)).containsExactly(3, 1, 4, 9, 2, 2, 3);
    }

    @Test
    void testTwo() {
        Iterator<Integer> iterator1 = Arrays.asList(9, 2, 3).iterator();
        Iterator<Integer> iterator2 = Arrays.asList(9, 4, 3, 5).iterator();
        ChainedIterator<Integer> chainedIterator = new ChainedIterator<>(iterator1, iterator2);
        assertThat(iterableOf(chainedIterator)).containsExactly(9, 2, 3, 9, 4, 3, 5);
    }

    @Test
    void testThree() {
        Iterator<Integer> iterator1 = Arrays.asList(7, 3, 5, 6).iterator();
        Iterator<Integer> iterator2 = Arrays.asList(5, 2).iterator();
        Iterator<Integer> iterator3 = Arrays.asList(7, 1, 5, 4).iterator();
        ChainedIterator<Integer> chainedIterator =
                new ChainedIterator<>(iterator1, iterator2, iterator3);
        assertThat(iterableOf(chainedIterator)).containsExactly(7, 3, 5, 6, 5, 2, 7, 1, 5, 4);
    }
    
    private <T> Iterable<T> iterableOf(Iterator<T> iterator) {
        return () -> iterator;
    }
    
}
