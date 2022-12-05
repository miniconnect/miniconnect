package hu.webarticum.miniconnect.rdmsframework.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.LargeInteger;

class RangeSelectionTest {

    @Test
    void testEmpty() {
        RangeSelection selection = new RangeSelection(0L, 0L);
        assertThat(selection).isEmpty();
        assertThat(selection.containsRow(LargeInteger.of(3L))).isFalse();
    }

    @Test
    void testAsc() {
        RangeSelection selection = new RangeSelection(
                LargeInteger.of(3L),
                LargeInteger.of(10L),
                true);
        assertThat(selection).containsExactly(larges(3L, 4L, 5L, 6L, 7L, 8L, 9L));
        assertThat(selection.containsRow(LargeInteger.of(0L))).isFalse();
        assertThat(selection.containsRow(LargeInteger.of(3L))).isTrue();
        assertThat(selection.containsRow(LargeInteger.of(7L))).isTrue();
        assertThat(selection.containsRow(LargeInteger.of(10L))).isFalse();
        assertThat(selection.containsRow(LargeInteger.of(15L))).isFalse();
    }

    @Test
    void testDesc() {
        RangeSelection selection = new RangeSelection(
                LargeInteger.of(3L),
                LargeInteger.of(10L),
                false);
        assertThat(selection).containsExactly(larges(9L, 8L, 7L, 6L, 5L, 4L, 3L));
        assertThat(selection.containsRow(LargeInteger.of(0L))).isFalse();
        assertThat(selection.containsRow(LargeInteger.of(3L))).isTrue();
        assertThat(selection.containsRow(LargeInteger.of(7L))).isTrue();
        assertThat(selection.containsRow(LargeInteger.of(10L))).isFalse();
        assertThat(selection.containsRow(LargeInteger.of(15L))).isFalse();
    }

    private LargeInteger[] larges(long... values) {
        LargeInteger[] result = new LargeInteger[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = LargeInteger.of(values[i]);
        }
        return result;
    }
    
}
