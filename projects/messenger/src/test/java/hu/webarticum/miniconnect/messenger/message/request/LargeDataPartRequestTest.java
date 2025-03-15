package hu.webarticum.miniconnect.messenger.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LargeDataPartRequestTest {
    
    @Test
    void testHashCodeAndEquals() {
        LargeDataHeadRequest instance1 = new LargeDataHeadRequest(7L, 3, "lorem", 42);
        LargeDataHeadRequest instance2 = new LargeDataHeadRequest(8L, 9, "ipsum", 35);
        LargeDataHeadRequest instance3 = new LargeDataHeadRequest(7L, 3, "lorem", 42);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
