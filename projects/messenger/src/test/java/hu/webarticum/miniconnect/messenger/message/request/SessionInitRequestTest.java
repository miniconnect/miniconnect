package hu.webarticum.miniconnect.messenger.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionInitRequestTest {
    
    @Test
    void testHashCodeAndEquals() {
        SessionInitRequest instance1 = new SessionInitRequest();
        SessionInitRequest instance2 = new SessionInitRequest();

        assertThat(instance1)
                .hasSameHashCodeAs(instance2)
                .isEqualTo(instance2);
    }

}
