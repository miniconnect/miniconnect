package hu.webarticum.miniconnect.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HeaderDataTest {
    
    @Test
    void testHashCodeAndEquals() {
        HeaderData instance1 = HeaderData.of(MessageType.QUERY_REQUEST, 584279845L, 347268);
        HeaderData instance2 = HeaderData.of(MessageType.QUERY_REQUEST, 4154L, 41748554);
        HeaderData instance3 = HeaderData.of(MessageType.QUERY_REQUEST, 584279845L, 347268);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
