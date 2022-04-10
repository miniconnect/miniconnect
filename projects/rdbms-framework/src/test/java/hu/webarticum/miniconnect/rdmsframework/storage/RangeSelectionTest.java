package hu.webarticum.miniconnect.rdmsframework.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class RangeSelectionTest {

    @Test
    void testEmpty() {
        RangeSelection selection = new RangeSelection(0L, 0L);
        assertThat(selection).isEmpty();
        assertThat(selection.containsRow(BigInteger.valueOf(3L))).isFalse();
    }

    @Test
    void testAsc() {
        OrderKey ascOrderKey = OrderKey.adHoc();
        RangeSelection selection = new RangeSelection(
                BigInteger.valueOf(3L),
                BigInteger.valueOf(10L),
                ascOrderKey,
                OrderKey.adHoc(),
                true);
        assertThat(selection).containsExactly(bigs(3L, 4L, 5L, 6L, 7L, 8L, 9L));
        assertThat(selection.containsRow(BigInteger.valueOf(0L))).isFalse();
        assertThat(selection.containsRow(BigInteger.valueOf(3L))).isTrue();
        assertThat(selection.containsRow(BigInteger.valueOf(7L))).isTrue();
        assertThat(selection.containsRow(BigInteger.valueOf(10L))).isFalse();
        assertThat(selection.containsRow(BigInteger.valueOf(15L))).isFalse();
    }

    @Test
    void testDesc() {
        OrderKey descOrderKey = OrderKey.adHoc();
        RangeSelection selection = new RangeSelection(
                BigInteger.valueOf(3L),
                BigInteger.valueOf(10L),
                OrderKey.adHoc(),
                descOrderKey,
                false);
        assertThat(selection).containsExactly(bigs(9L, 8L, 7L, 6L, 5L, 4L, 3L));
        assertThat(selection.containsRow(BigInteger.valueOf(0L))).isFalse();
        assertThat(selection.containsRow(BigInteger.valueOf(3L))).isTrue();
        assertThat(selection.containsRow(BigInteger.valueOf(7L))).isTrue();
        assertThat(selection.containsRow(BigInteger.valueOf(10L))).isFalse();
        assertThat(selection.containsRow(BigInteger.valueOf(15L))).isFalse();
    }

    private BigInteger[] bigs(long... values) {
        BigInteger[] result = new BigInteger[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = BigInteger.valueOf(values[i]);
        }
        return result;
    }
    
}
