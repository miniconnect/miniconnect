package hu.webarticum.miniconnect.jdbc.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class GeneralConverterTest {
    
    private final GeneralConverter generalConverter = new GeneralConverter();
    
    
    @Test
    void testBoolean() throws Exception {
        assertThat(generalConverter.convert(true, Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert(false, Boolean.class, null)).isFalse();
        assertThat(generalConverter.convert(0, Boolean.class, null)).isFalse();
        assertThat(generalConverter.convert(0.2, Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert(-0.3f, Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert(11, Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert(-3L, Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert("", Boolean.class, null)).isFalse();
        assertThat(generalConverter.convert("0", Boolean.class, null)).isFalse();
        assertThat(generalConverter.convert("0.0", Boolean.class, null)).isTrue();
        assertThat(generalConverter.convert("NO", Boolean.class, null)).isFalse();
        assertThat(generalConverter.convert("true", Boolean.class, null)).isTrue();
    }

    @Test
    void testByte() throws Exception {
        assertThat(generalConverter.convert(false, Byte.class, null)).isZero();
        assertThat(generalConverter.convert(true, Byte.class, null)).isOne();
        assertThat(generalConverter.convert(7, Byte.class, null)).isEqualTo((byte) 7);
        assertThat(generalConverter.convert(-3.2, Byte.class, null)).isEqualTo((byte) -3);
        assertThat(generalConverter.convert("9", Byte.class, null)).isEqualTo((byte) 9);
    }

    @Test
    void testCharacter() throws Exception {
        assertThat(generalConverter.convert(false, Character.class, null)).isEqualTo('f');
        assertThat(generalConverter.convert(true, Character.class, null)).isEqualTo('t');
        assertThat(generalConverter.convert(46, Character.class, null)).isEqualTo('.');
        assertThat(generalConverter.convert('S', Character.class, null)).isEqualTo('S');
        assertThat(generalConverter.convert("B", Character.class, null)).isEqualTo('B');
        assertThat(generalConverter.convert("xyz", Character.class, null)).isEqualTo('x');
        assertThat(generalConverter.convert("", Character.class, null)).isNull();
    }

    @Test
    void testShort() throws Exception {
        assertThat(generalConverter.convert(false, Short.class, null)).isZero();
        assertThat(generalConverter.convert(true, Short.class, null)).isOne();
        assertThat(generalConverter.convert(2L, Short.class, null)).isEqualTo((short) 2);
        assertThat(generalConverter.convert(-1.0, Short.class, null)).isEqualTo((short) -1);
        assertThat(generalConverter.convert("4", Short.class, null)).isEqualTo((short) 4);
    }
    
    @Test
    void testInteger() throws Exception {
        assertThat(generalConverter.convert(false, Integer.class, null)).isZero();
        assertThat(generalConverter.convert(true, Integer.class, null)).isOne();
        assertThat(generalConverter.convert(5, Integer.class, null)).isEqualTo(5);
        assertThat(generalConverter.convert(2.1f, Integer.class, null)).isEqualTo(2);
        assertThat(generalConverter.convert("5", Integer.class, null)).isEqualTo(5);
    }

    @Test
    void testLong() throws Exception {
        assertThat(generalConverter.convert(false, Long.class, null)).isZero();
        assertThat(generalConverter.convert(true, Long.class, null)).isOne();
        assertThat(generalConverter.convert(1, Long.class, null)).isEqualTo(1L);
        assertThat(generalConverter.convert(4.3f, Long.class, null)).isEqualTo(4L);
        assertThat(generalConverter.convert("65", Long.class, null)).isEqualTo(65L);
    }

    @Test
    void testFloat() throws Exception {
        assertThat(generalConverter.convert(false, Float.class, null)).isZero();
        assertThat(generalConverter.convert(true, Float.class, null)).isOne();
        assertThat(generalConverter.convert(-7, Float.class, null)).isEqualTo(-7f);
        assertThat(generalConverter.convert(2.0, Float.class, null)).isEqualTo(2f);
        assertThat(generalConverter.convert("8", Float.class, null)).isEqualTo(8f);
    }

    @Test
    void testDouble() throws Exception {
        assertThat(generalConverter.convert(false, Double.class, null)).isZero();
        assertThat(generalConverter.convert(true, Double.class, null)).isOne();
        assertThat(generalConverter.convert(2, Double.class, null)).isEqualTo(2.0);
        assertThat(generalConverter.convert(-1.3f, Double.class, null))
                .isCloseTo(-1.3, within(0.000001));
        assertThat(generalConverter.convert("10", Double.class, null)).isEqualTo(10.0);
    }
    
    @Test
    void testBytes() throws Exception {
        assertThat(generalConverter.convert(new byte[] { (byte) 2, (byte) 5 }, byte[].class, null))
                .containsExactly(2, 5);
    }

    @Test
    void testString() throws Exception {
        assertThat(generalConverter.convert(true, String.class, null)).isEqualTo("true");
        assertThat(generalConverter.convert(false, String.class, null)).isEqualTo("false");
        assertThat(generalConverter.convert(12, String.class, null)).isEqualTo("12");
        assertThat(generalConverter.convert(7L, String.class, null)).isEqualTo("7");
        assertThat(generalConverter.convert("lorem", String.class, null)).isEqualTo("lorem");
    }
    
}
