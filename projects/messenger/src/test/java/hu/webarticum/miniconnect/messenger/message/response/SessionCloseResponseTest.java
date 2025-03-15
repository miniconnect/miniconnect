package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionCloseResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        SessionCloseResponse instance1 = new SessionCloseResponse(2L, 5);
        SessionCloseResponse instance2 = new SessionCloseResponse(7L, 4);
        SessionCloseResponse instance3 = new SessionCloseResponse(2L, 5);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
