package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;

class ResultResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        ResultSetRowsResponse instance1 = buildResultSetRowsResponse1();
        ResultSetRowsResponse instance2 = buildResultSetRowsResponse2();
        ResultSetRowsResponse instance3 = buildResultSetRowsResponse1();

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

    private ResultSetRowsResponse buildResultSetRowsResponse1() {
        ImmutableList<Integer> nullables = ImmutableList.of(2);
        Map<Integer, Integer> fixedSizesBuilder = new HashMap<>();
        fixedSizesBuilder.put(0, 4);
        fixedSizesBuilder.put(1, 4);
        ImmutableMap<Integer, Integer> fixedSizes = new ImmutableMap<>(fixedSizesBuilder);
        ImmutableList<ImmutableList<CellData>> rows = ImmutableList.of(
                ImmutableList.of(
                        new CellData(false, 4, ByteString.of("xxxx")),
                        new CellData(false, 4, ByteString.of("yyyy")),
                        new CellData(true, 0, ByteString.empty())));
        return new ResultSetRowsResponse(3L, 2, 4L, nullables, fixedSizes, rows);
    }

    private ResultSetRowsResponse buildResultSetRowsResponse2() {
        ImmutableList<Integer> nullables = ImmutableList.empty();
        Map<Integer, Integer> fixedSizesBuilder = new HashMap<>();
        fixedSizesBuilder.put(0, 5);
        ImmutableMap<Integer, Integer> fixedSizes = new ImmutableMap<>(fixedSizesBuilder);
        ImmutableList<ImmutableList<CellData>> rows = ImmutableList.of(
                ImmutableList.of(
                        new CellData(false, 5, ByteString.of("lorem")),
                        new CellData(false, 5, ByteString.of("ipsum"))),
                ImmutableList.of(
                        new CellData(false, 5, ByteString.of("12345")),
                        new CellData(false, 9, ByteString.of("abcdefghi"))));
        return new ResultSetRowsResponse(2L, 5, 7L, nullables, fixedSizes, rows);
    }

}
