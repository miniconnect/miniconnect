package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResultSetEofResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        ResultSetEofResponse instance1 = new ResultSetEofResponse(5L, 9, 12L);
        ResultSetEofResponse instance2 = new ResultSetEofResponse(3L, 6, 94L);
        ResultSetEofResponse instance3 = new ResultSetEofResponse(5L, 9, 12L);

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
