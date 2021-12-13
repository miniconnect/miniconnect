package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.util.data.ImmutableList;

class DefaultComparatorTest {

    @Test
    void testBothEmpty() {
        ImmutableList<Object> values1 = ImmutableList.empty();
        ImmutableList<Object> values2 = ImmutableList.empty();
        assertThat(new DefaultComparator().compare(values1, values2)).isZero();
    }

    @Test
    void testFirstEmpty() {
        ImmutableList<Object> values1 = ImmutableList.empty();
        ImmutableList<Object> values2 = ImmutableList.of("bar", 2);
        assertThat(new DefaultComparator().compare(values1, values2)).isNegative();
    }

    @Test
    void testSecondEmpty() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.empty();
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testBothSingleEqual() {
        ImmutableList<Object> values1 = ImmutableList.of("foo");
        ImmutableList<Object> values2 = ImmutableList.of("foo");
        assertThat(new DefaultComparator().compare(values1, values2)).isZero();
    }

    @Test
    void testBothSingleFirstGreater() {
        ImmutableList<Object> values1 = ImmutableList.of("foo");
        ImmutableList<Object> values2 = ImmutableList.of("bar");
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testBothSingleSecondGreater() {
        ImmutableList<Object> values1 = ImmutableList.of("bar");
        ImmutableList<Object> values2 = ImmutableList.of("foo");
        assertThat(new DefaultComparator().compare(values1, values2)).isNegative();
    }

    @Test
    void testBothTwoEqual() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.of("foo", 1);
        assertThat(new DefaultComparator().compare(values1, values2)).isZero();
    }

    @Test
    void testBothTwoDifferent1() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.of("bar", 1);
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testBothTwoDifferent2() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 5);
        ImmutableList<Object> values2 = ImmutableList.of("foo", 1);
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testBothTwoDifferent3() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.of("bar", 2);
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testSamePrefixFirstLonger() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1, 2.5);
        ImmutableList<Object> values2 = ImmutableList.of("foo", 1);
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

    @Test
    void testSamePrefixSecondLonger() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.of("foo", 1, 0.3);
        assertThat(new DefaultComparator().compare(values1, values2)).isNegative();
    }

    @Test
    void testVeryDifferent() {
        ImmutableList<Object> values1 = ImmutableList.of("foo", 1);
        ImmutableList<Object> values2 = ImmutableList.of("bar", 2, 0.3);
        assertThat(new DefaultComparator().compare(values1, values2)).isPositive();
    }

}
