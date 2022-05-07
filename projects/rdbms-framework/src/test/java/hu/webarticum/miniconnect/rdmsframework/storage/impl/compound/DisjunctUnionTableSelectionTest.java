package hu.webarticum.miniconnect.rdmsframework.storage.impl.compound;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleSelection;

class DisjunctUnionTableSelectionTest {

    @Test
    void testEmpty() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of();
        assertThat(selection.iterator()).isExhausted();
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isFalse();
    }

    @Test
    void testOfEmpty() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of(
                new SimpleSelection(ImmutableList.empty()));
        assertThat(selection.iterator()).isExhausted();
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isFalse();
    }

    @Test
    void testOfEmpties() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of(
                new SimpleSelection(ImmutableList.empty()));
        assertThat(selection.iterator()).isExhausted();
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isFalse();
    }

    @Test
    void testOfOne() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of(
                new SimpleSelection(bigsOf(2, 4, 6, 7, 10)));
        assertThat(selection).containsExactly(bigs(2, 4, 6, 7, 10));
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isTrue();
    }

    @Test
    void testOfTwo() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of(
                new SimpleSelection(bigsOf(3, 6, 9)),
                new SimpleSelection(bigsOf(4, 8, 12)));
        assertThat(selection).containsExactly(bigs(3, 6, 9, 4, 8, 12));
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isFalse();
        assertThat(selection.containsRow(big(3))).isTrue();
        assertThat(selection.containsRow(big(4))).isTrue();
    }

    @Test
    void testOfMany() {
        DisjunctUnionTableSelection selection = DisjunctUnionTableSelection.of(
                new SimpleSelection(bigsOf(3, 6, 9)),
                new SimpleSelection(bigsOf(4, 8, 12)),
                new SimpleSelection(bigsOf()),
                new SimpleSelection(bigsOf(5)),
                new SimpleSelection(bigsOf(10, 20)));
        assertThat(selection).containsExactly(bigs(3, 6, 9, 4, 8, 12, 5, 10, 20));
        assertThat(selection.containsRow(big(1))).isFalse();
        assertThat(selection.containsRow(big(2))).isFalse();
        assertThat(selection.containsRow(big(3))).isTrue();
        assertThat(selection.containsRow(big(4))).isTrue();
        assertThat(selection.containsRow(big(7))).isFalse();
        assertThat(selection.containsRow(big(10))).isTrue();
        assertThat(selection.containsRow(big(15))).isFalse();
        assertThat(selection.containsRow(big(20))).isTrue();
        assertThat(selection.containsRow(big(30))).isFalse();
    }

    protected BigInteger big(long number) {
        return BigInteger.valueOf(number);
    }

    protected BigInteger[] bigs(long... numbers) {
        return Arrays.stream(numbers).mapToObj(this::big).toArray(BigInteger[]::new);
    }

    protected ImmutableList<BigInteger> bigsOf(long... numbers) {
        return Arrays.stream(numbers).mapToObj(this::big).collect(ImmutableList.createCollector());
    }
    
}
