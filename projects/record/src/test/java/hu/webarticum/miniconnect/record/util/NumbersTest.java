package hu.webarticum.miniconnect.record.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;

class NumbersTest {

    @Test
    void testBigDecimalToBigDecimal() {
        Map<BigDecimal, Integer> inputs = new LinkedHashMap<>();
        inputs.put(new BigDecimal("35"), 0);
        inputs.put(new BigDecimal("7.2"), 5);
        inputs.put(new BigDecimal("7.1"), 0);
        inputs.put(new BigDecimal("7.3"), 1);
        inputs.put(new BigDecimal("8.55"), 1);
        inputs.put(new BigDecimal("192.34"), -1);
        ImmutableList<String> actual = inputs.entrySet().stream()
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString)
                .collect(ImmutableList.createCollector());
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
        Map<BigInteger, Integer> inputs = new LinkedHashMap<>();
        inputs.put(new BigInteger("8"), 0);
        inputs.put(new BigInteger("15"), 2);
        inputs.put(new BigInteger("234"), -1);
        ImmutableList<String> actual = inputs.entrySet().stream()
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString)
                .collect(ImmutableList.createCollector());
        assertThat(actual).containsExactly(
                "8",
                "15.00",
                "230");
    }

    @Test
    void testIntegerToBigDecimal() {
        Map<Integer, Integer> inputs = new LinkedHashMap<>();
        inputs.put(0, 0);
        inputs.put(3, 0);
        inputs.put(14, 2);
        inputs.put(231, -1);
        ImmutableList<String> actual = inputs.entrySet().stream()
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString)
                .collect(ImmutableList.createCollector());
        assertThat(actual).containsExactly(
                "0",
                "3",
                "14.00",
                "230");
    }

    @Test
    void testDoubleToBigDecimal() {
        Map<Double, Integer> inputs = new LinkedHashMap<>();
        inputs.put(0d, 0);
        inputs.put(7.234d, 0);
        inputs.put(12.34672d, 4);
        inputs.put(15.3d, 1);
        inputs.put(19.5d, 2);
        inputs.put(23.23d, 1);
        inputs.put(123d, -1);
        ImmutableList<String> actual = inputs.entrySet().stream()
                .map(e -> Numbers.toBigDecimal(e.getKey(), e.getValue()))
                .map(BigDecimal::toPlainString)
                .collect(ImmutableList.createCollector());
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
