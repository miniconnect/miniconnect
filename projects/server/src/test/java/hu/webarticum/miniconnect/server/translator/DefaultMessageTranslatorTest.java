package hu.webarticum.miniconnect.server.translator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionCloseRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ErrorData;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionCloseResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.MessageType;

class DefaultMessageTranslatorTest {
    
    @Test
    void testAllBackAndForth() {
        DefaultMessageTranslator translator = new DefaultMessageTranslator();
        List<Message> messages = Arrays.asList(
                createSessionInitRequest(),
                createQueryRequest(),
                createLargeDataHeadRequest(),
                createLargeDataPartRequest(),
                createSessionCloseRequest(),
                createSessionInitResponse(),
                createResultResponse(),
                createResultSetRowsResponse(),
                createResultSetValuePartResponse(),
                createResultSetEofResponse(),
                createLargeDataSaveResponse(),
                createSessionCloseResponse());
        List<MessageType> messageTypes = messages.stream()
                .map(m -> MessageType.ofMessage(m))
                .collect(Collectors.toList());
        List<Message> recoveredMessages = messages.stream()
                .map(translator::encode)
                .map(translator::decode)
                .collect(Collectors.toList());

        assertThat(messageTypes).contains(MessageType.values());
        assertThat(recoveredMessages).isEqualTo(messages);
    }
    
    private Message createSessionInitRequest() {
        return new SessionInitRequest();
    }

    private Message createQueryRequest() {
        return new QueryRequest(77L, 13, "SELECT 1");
    }

    private Message createLargeDataHeadRequest() {
        return new LargeDataHeadRequest(9L, 45, "xxx", 12);
    }

    private Message createLargeDataPartRequest() {
        return new LargeDataPartRequest(23L, 2, 43L, ByteString.of("abc"));
    }

    private Message createSessionCloseRequest() {
        return new SessionCloseRequest(5L, 3);
    }

    private Message createSessionInitResponse() {
        return new SessionInitResponse(9L);
    }

    private Message createResultResponse() {
        ErrorData error = new ErrorData(3, "00003", "Error");
        ImmutableList<ErrorData> warnings = ImmutableList.of(
                new ErrorData(7, "00007", "Warning1"),
                new ErrorData(8, "00008", "Warning2"),
                new ErrorData(8, "00008", "Warning3"));
        Map<String, ByteString> propertiesBuilder = new HashMap<>();
        propertiesBuilder.put("key1", ByteString.of("value1"));
        propertiesBuilder.put("key2", ByteString.of("value2"));
        propertiesBuilder.put("key3", ByteString.of("value3"));
        ImmutableMap<String, ByteString> properties = ImmutableMap.fromMap(propertiesBuilder);
        int dynamicSize = MiniValueDefinition.DYNAMIC_SIZE;
        ImmutableList<ColumnHeaderData> columnHeaders = ImmutableList.of(
                new ColumnHeaderData(
                        "id", false, Integer.BYTES, "INT", ImmutableMap.empty()),
                new ColumnHeaderData(
                        "label", true, dynamicSize, "VARCHAR(30)", ImmutableMap.empty()),
                new ColumnHeaderData(
                        "description", false, dynamicSize, "TEXT", properties));
        return new ResultResponse(4L, 3, false, error, warnings, true, columnHeaders);
    }
    
    private Message createResultSetRowsResponse() {
        ImmutableList<Integer> nullables = ImmutableList.of(2);
        Map<Integer, Integer> fixedSizesBuilder = new HashMap<>();
        fixedSizesBuilder.put(0, 4);
        fixedSizesBuilder.put(1, 4);
        ImmutableMap<Integer, Integer> fixedSizes = ImmutableMap.fromMap(fixedSizesBuilder);
        ImmutableList<ImmutableList<CellData>> rows = ImmutableList.of(
                ImmutableList.of(
                        new CellData(false, 4, ByteString.of("abcd")),
                        new CellData(false, 4, ByteString.of("1234")),
                        new CellData(true, 0, ByteString.empty())));
        return new ResultSetRowsResponse(5L, 9, 12L, nullables, fixedSizes, rows);
    }
    
    private Message createResultSetValuePartResponse() {
        return new ResultSetValuePartResponse(3L, 11, 52L, 3, 0L, ByteString.of("lorem"));
    }
    
    private Message createResultSetEofResponse() {
        return new ResultSetEofResponse(2L, 12, 54L);   
    }
    
    private Message createLargeDataSaveResponse() {
        return new LargeDataSaveResponse(2L, 3, false, 1, "00001", "Error");
    }

    private Message createSessionCloseResponse() {
        return new SessionCloseResponse(2L, 5);
    }

}
