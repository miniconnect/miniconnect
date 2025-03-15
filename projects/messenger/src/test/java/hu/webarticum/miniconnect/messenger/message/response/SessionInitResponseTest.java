package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionInitResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        SessionInitResponse instance1 = new SessionInitResponse(3L);
        SessionInitResponse instance2 = new SessionInitResponse(9L);
        SessionInitResponse instance3 = new SessionInitResponse(3L);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
