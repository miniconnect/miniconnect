package hu.webarticum.miniconnect.server.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class StrictlySortedQueueTest {

    private StrictlySortedQueue<Integer> queue;


    @BeforeEach
    void init() {
        queue = new StrictlySortedQueue<Integer>(
                (f, p, i) -> i == Objects.requireNonNullElse(p, 0) + 1);
    }

    @Test
    void testEmpty() {
        assertThat(queue.available()).isFalse();
    }

    @Test
    @Timeout(5)
    void testSingleItemSynchronously() throws InterruptedException {
        queue.add(1);

        assertThat(queue.available()).isTrue();

        Integer item1 = queue.take();

        assertThat(item1).isEqualTo(1);
        assertThat(queue.available()).isFalse();
    }

    @Test
    @Timeout(5)
    void testFlippedItemsSynchronously() throws InterruptedException {
        queue.add(2);

        assertThat(queue.available()).isFalse();

        queue.add(1);

        assertThat(queue.available()).isTrue();

        Integer item1 = queue.take();

        assertThat(item1).isEqualTo(1);
        assertThat(queue.available()).isTrue();

        Integer item2 = queue.take();

        assertThat(item2).isEqualTo(2);
        assertThat(queue.available()).isFalse();
    }

    @Test
    @Timeout(5)
    void testManyItemsSynchronously() throws InterruptedException {
        int[] unorderedItems = new int[] { 4, 2, -4, 7, 3, 1, 8 };
        for (int item : unorderedItems) {
            queue.add(item);
        }

        assertThat(queue.available()).isTrue();
        assertThat(queue.take()).isEqualTo(1);
        assertThat(queue.take()).isEqualTo(2);
        assertThat(queue.take()).isEqualTo(3);
        assertThat(queue.take()).isEqualTo(4);
        assertThat(queue.available()).isFalse();
    }

    @Test
    @Timeout(10)
    void testManyItemsAsynchronously() throws InterruptedException {
        int[] unorderedItems = new int[] {
                7, 2, -4, 9, 12, -1, 16, 0, 5, 2, 6, 4, 8, 3, 15, 1, 14, -3 };

        new Thread(() -> {
            for (int item : unorderedItems) {
                try {
                    Thread.sleep(3); // NOSONAR
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                queue.add(item);
            }
        }).start();

        assertThat(queue.take()).isEqualTo(1);
        assertThat(queue.take()).isEqualTo(2);
        assertThat(queue.take()).isEqualTo(3);
        assertThat(queue.take()).isEqualTo(4);
        assertThat(queue.take()).isEqualTo(5);
        assertThat(queue.take()).isEqualTo(6);
        assertThat(queue.take()).isEqualTo(7);
        assertThat(queue.take()).isEqualTo(8);
        assertThat(queue.take()).isEqualTo(9);
    }

}
