package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;

class DiffTableUtilTest {

    @Test
    void testEmptyToEmpty() {
        ImmutableList<Integer> existingValues = ImmutableList.empty();
        ImmutableList<Integer> newValues = ImmutableList.empty();
        Comparator<Integer> comparator = Comparator.naturalOrder();
        assertThat(DiffTableUtil.mergeUnique(existingValues, newValues, comparator)).isEqualTo(ImmutableList.empty());
    }

    @Test
    void testNoDifferentNew() {
        ImmutableList<Integer> existingValues = ImmutableList.of(1, 3, 4, 7, 10, 11, 12);
        ImmutableList<Integer> newValues = ImmutableList.of(3, 7, 3, 11, 11);
        Comparator<Integer> comparator = Comparator.naturalOrder();
        assertThat(DiffTableUtil.mergeUnique(existingValues, newValues, comparator)).isEqualTo(ImmutableList.of(
                1, 3, 4, 7, 10, 11, 12));
    }
    
    @Test
    void testComplexMerge() {
        ImmutableList<Integer> existingValues = ImmutableList.of(1, 3, 4, 7, 10, 11, 12);
        ImmutableList<Integer> newValues = ImmutableList.of(5, 3, -2, 7, 12, 2, 21, 21, 3, -1, 0, 0, 1, 7, 3);
        Comparator<Integer> comparator = Comparator.naturalOrder();
        assertThat(DiffTableUtil.mergeUnique(existingValues, newValues, comparator)).isEqualTo(ImmutableList.of(
                -2, -1, 0, 1, 2, 3, 4, 5, 7, 10, 11, 12, 21));
    }
    
}
