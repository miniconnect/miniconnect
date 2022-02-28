package hu.webarticum.miniconnect.record.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StandardValueTypeTest {

    @Test
    void testFlags() throws Exception {
        assertThat(StandardValueType.values())
                .extracting(t -> t.flag())
                .doesNotHaveDuplicates()
                .allMatch(f -> f.length() == StandardValueType.FLAG_LENGTH);
    }

    @Test
    void testNames() throws Exception {
        assertThat(StandardValueType.values())
                .allMatch(t -> t.defaultTranslator().name().equals(t.name()));
    }
    
}
