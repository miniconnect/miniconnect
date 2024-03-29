package hu.webarticum.miniconnect.record.customvalue.schema;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.record.custom.schema.MetaType;

class MetaTypeTest {

    @Test
    void testFlags() throws Exception {
        assertThat(MetaType.values())
                .extracting(t -> t.flag())
                .doesNotHaveDuplicates();
    }
    
}
