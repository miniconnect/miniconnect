package hu.webarticum.miniconnect.server.translator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.MessageType;
import hu.webarticum.miniconnect.util.data.ByteString;

class DefaultMessageTranslatorTest {
    
    @Test
    void testAllBackAndForth() {
        DefaultMessageTranslator translator = new DefaultMessageTranslator();
        List<Message> messages = Arrays.asList(
                createQueryRequest(),
                createLargeDataHeadRequest(),
                createLargeDataPartRequest());
        List<MessageType> messageTypes = messages.stream()
                .map(m -> MessageType.ofMessage(m))
                .collect(Collectors.toList());
        List<Message> recoveredMessages = messages.stream()
                .map(translator::encode)
                .map(translator::decode)
                .collect(Collectors.toList());

        //assertThat(messageTypes).contains(MessageType.values());
        assertThat(recoveredMessages).isEqualTo(messages);
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

}
