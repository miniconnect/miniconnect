package hu.webarticum.miniconnect.record.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;

class NumbersTest {

    @Test
    void testBigDecimalToBigDecimal() {
        ImmutableList<Map.Entry<BigDecimal, Integer>> inputs = ImmutableList.of(
                Map.entry(new BigDecimal("35"), 0),
                Map.entry(new BigDecimal("7.2"), 5),
                Map.entry(new BigDecimal("7.1"), 0),
                Map.entry(new BigDecimal("7.3"), 1),
                Map.entry(new BigDecimal("8.55"), 1),
                Map.entry(new BigDecimal("192.34"), -1));
        ImmutableList<String> actual = inputs
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString);
        assertThat(actual).containsExactly(
                "35",
                "7.20000",
                "7",
                "7.3",
                "8.6",
                "190");
    }

    @Test
    void testBigIntegerToBigDecimal() {
        ImmutableList<Map.Entry<BigInteger, Integer>> inputs = ImmutableList.of(
                Map.entry(new BigInteger("8"), 0),
                Map.entry(new BigInteger("15"), 2),
                Map.entry(new BigInteger("234"), -1));
        ImmutableList<String> actual = inputs
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString);
        assertThat(actual).containsExactly(
                "8",
                "15.00",
                "230");
    }

    @Test
    void testIntegerToBigDecimal() {
        ImmutableList<Map.Entry<Integer, Integer>> inputs = ImmutableList.of(
                Map.entry(0, 0),
                Map.entry(3, 0),
                Map.entry(14, 2),
                Map.entry(231, -1));
        ImmutableList<String> actual = inputs
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString);
        assertThat(actual).containsExactly(
                "0",
                "3",
                "14.00",
                "230");
    }

    @Test
    void testDoubleToBigDecimal() {
        ImmutableList<Map.Entry<Double, Integer>> inputs = ImmutableList.of(
                Map.entry(0d, 0),
                Map.entry(7.234d, 0),
                Map.entry(12.34672d, 4),
                Map.entry(15.3d, 1),
                Map.entry(19.5d, 2),
                Map.entry(23.23d, 1),
                Map.entry(123d, -1));
        ImmutableList<String> actual = inputs
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString);
        assertThat(actual).containsExactly(
                "0",
                "7",
                "12.3467",
                "15.3",
                "19.50",
                "23.2",
                "120");
    }

}
