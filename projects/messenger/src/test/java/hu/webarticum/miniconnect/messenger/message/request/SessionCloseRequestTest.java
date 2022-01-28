package hu.webarticum.miniconnect.messenger.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionCloseRequestTest {
    
    @Test
    void testHashCodeAndEquals() {
        SessionCloseRequest instance1 = new SessionCloseRequest(32L, 4);
        SessionCloseRequest instance2 = new SessionCloseRequest(25L, 2);
        SessionCloseRequest instance3 = new SessionCloseRequest(32L, 4);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
