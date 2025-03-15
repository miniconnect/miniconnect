package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ByteString;

class ResultSetValuePartResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        ResultSetValuePartResponse instance1 = 
                new ResultSetValuePartResponse(3L, 7, 7L, 2, 0L, ByteString.of("111"));
        ResultSetValuePartResponse instance2 = 
                new ResultSetValuePartResponse(5L, 2, 9L, 0, 1L, ByteString.of("222"));
        ResultSetValuePartResponse instance3 = 
                new ResultSetValuePartResponse(3L, 7, 7L, 2, 0L, ByteString.of("111"));

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
