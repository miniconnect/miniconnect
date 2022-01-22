package hu.webarticum.miniconnect.messenger.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.util.data.ByteString;

class LargeDataHeadRequestTest {
    
    @Test
    void testHashCodeAndEquals() {
        LargeDataPartRequest instance1 = new LargeDataPartRequest(4L, 9, 12L, ByteString.of("123"));
        LargeDataPartRequest instance2 = new LargeDataPartRequest(3L, 2, 30L, ByteString.of("abc"));
        LargeDataPartRequest instance3 = new LargeDataPartRequest(4L, 9, 12L, ByteString.of("123"));

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
