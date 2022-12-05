package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.LargeInteger;

class SequenceTest {

    @Test
    void testNegativeUntil() {
        assertThatThrownBy(() -> new Sequence(-10)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testEmpty() {
        assertThat(new Sequence(0)).isEmpty();
    }

    @Test
    void testSingle() {
        assertThat(new Sequence(1)).containsExactly(bigs(0));
    }

    @Test
    void testUntilFive() {
        assertThat(new Sequence(5)).containsExactly(bigs(0, 1, 2, 3, 4));
    }

    
    private LargeInteger[] bigs(int... values) {
        LargeInteger[] result = new LargeInteger[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = LargeInteger.of(values[i]);
        }
        return result;
    }
    
}
