package hu.webarticum.miniconnect.messenger.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QueryRequestTest {
    
    @Test
    void testHashCodeAndEquals() {
        QueryRequest instance1 = new QueryRequest(77L, 13, "SELECT 1");
        QueryRequest instance2 = new QueryRequest(135L, 2, "UPDATE data SET x=1");
        QueryRequest instance3 = new QueryRequest(77L, 13, "SELECT 1");

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
