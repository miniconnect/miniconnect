package hu.webarticum.miniconnect.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HeaderDataTest {
    
    @Test
    void testHashCodeAndEquals() {
        HeaderData headerData1 = HeaderData.of(MessageType.QUERY_REQUEST, 584279845L, 347268);
        HeaderData headerData2 = HeaderData.of(MessageType.QUERY_REQUEST, 4154L, 41748554);
        HeaderData headerData3 = HeaderData.of(MessageType.QUERY_REQUEST, 584279845L, 347268);

        assertThat(headerData1)
                .hasSameHashCodeAs(headerData3)
                .isNotEqualTo(headerData2)
                .isEqualTo(headerData3);
        assertThat(headerData2)
                .isNotEqualTo(headerData3);
    }

}
