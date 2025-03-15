package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LargeDataSaveResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        LargeDataSaveResponse instance1 =
                new LargeDataSaveResponse(5L, 9, false, 4, "00004", "Oops");
        LargeDataSaveResponse instance2 =
                new LargeDataSaveResponse(3L, 5, true, 0, "00000", "");
        LargeDataSaveResponse instance3 =
                new LargeDataSaveResponse(5L, 9, false, 4, "00004", "Oops");

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

}
