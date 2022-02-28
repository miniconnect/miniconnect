package hu.webarticum.miniconnect.messenger.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ErrorData;

class ResultSetRowsResponseTest {
    
    @Test
    void testHashCodeAndEquals() {
        ResultResponse instance1 = buildResultResponse1();
        ResultResponse instance2 = buildResultResponse2();
        ResultResponse instance3 = buildResultResponse1();

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }

    private ResultResponse buildResultResponse1() {
        ErrorData error = new ErrorData(2, "00002", "Error");
        ImmutableList<ErrorData> warnings = ImmutableList.of(
                new ErrorData(4, "00004", "Warning1"),
                new ErrorData(5, "00005", "Warning2"),
                new ErrorData(6, "00006", "Warning3"));
        Map<String, ByteString> propertiesBuilder = new HashMap<>();
        propertiesBuilder.put("key1", ByteString.of("item1"));
        propertiesBuilder.put("key2", ByteString.of("item2"));
        propertiesBuilder.put("key3", ByteString.of("item3"));
        ImmutableMap<String, ByteString> properties = ImmutableMap.fromMap(propertiesBuilder);
        int dynamicLength = MiniValueDefinition.DYNAMIC_LENGTH;
        ImmutableList<ColumnHeaderData> columnHeaders = ImmutableList.of(
                new ColumnHeaderData(
                        "id", false, Integer.BYTES, "INT", ImmutableMap.empty()),
                new ColumnHeaderData(
                        "label", true, dynamicLength, "VARCHAR(50)", ImmutableMap.empty()),
                new ColumnHeaderData(
                        "description", false, dynamicLength, "TEXT", properties));
        return new ResultResponse(3L, 2, false, error, warnings, true, columnHeaders);
    }

    private ResultResponse buildResultResponse2() {
        ErrorData error = new ErrorData(0, "00000", "");
        ImmutableList<ErrorData> warnings = ImmutableList.of(
                new ErrorData(10, "00010", "Warning1"),
                new ErrorData(11, "00011", "Warning2"));
        return new ResultResponse(5L, 0, true, error, warnings, false, ImmutableList.empty());
    }

}
