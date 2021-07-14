package hu.webarticum.miniconnect.jdbc.converter;

import static org.assertj.core.api.Assertions.assertThat;

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
    
}
