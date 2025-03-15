package hu.webarticum.miniconnect.server.translator;

import java.util.EnumMap;
import java.util.Map;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderDecoder;
import hu.webarticum.miniconnect.server.MessageTranslator;
import hu.webarticum.miniconnect.server.MessageType;
import hu.webarticum.miniconnect.transfer.Packet;

public class DefaultMessageTranslator implements MessageTranslator {

    private static final Map<MessageType, TranslatorDriver> drivers =
            new EnumMap<>(MessageType.class);
    static {
        // with this switch based solution we can statically detect missing drivers
        for (MessageType messageType : MessageType.values()) {
            switch (messageType) {
                case SESSION_INIT_REQUEST:
                    drivers.put(messageType, new SessionInitRequestTranslatorDriver());
                    break;
                case QUERY_REQUEST:
                    drivers.put(messageType, new QueryRequestTranslatorDriver());
                    break;
                case LARGE_DATA_HEAD_REQUEST:
                    drivers.put(messageType, new LargeDataHeadRequestTranslatorDriver());
                    break;
                case LARGE_DATA_PART_REQUEST:
                    drivers.put(messageType, new LargeDataPartRequestTranslatorDriver());
                    break;
                case SESSION_CLOSE_REQUEST:
                    drivers.put(messageType, new SessionCloseRequestTranslatorDriver());
                    break;
                case SESSION_INIT_RESPONSE:
                    drivers.put(messageType, new SessionInitResponseTranslatorDriver());
                    break;
                case RESULT_RESPONSE:
                    drivers.put(messageType, new ResultResponseTranslatorDriver());
                    break;
                case RESULT_SET_ROWS_RESPONSE:
                    drivers.put(messageType, new ResultSetRowsResponseTranslatorDriver());
                    break;
                case RESULT_SET_VALUE_PART_RESPONSE:
                    drivers.put(messageType, new ResultSetValuePartResponseTranslatorDriver());
                    break;
                case RESULT_SET_EOF_RESPONSE:
                    drivers.put(messageType, new ResultSetEofResponseTranslatorDriver());
                    break;
                case LARGE_DATA_SAVE_RESPONSE:
                    drivers.put(messageType, new LargeDataSaveResponseTranslatorDriver());
                    break;
                case SESSION_CLOSE_RESPONSE:
                    drivers.put(messageType, new SessionCloseResponseTranslatorDriver());
                    break;
            }
        }
    }
    
    
    @Override
    public Message decode(Packet packet) {
        HeaderData header = new HeaderDecoder().decode(packet.header());
        MessageType messageType = header.messageType();
        TranslatorDriver driver = drivers.get(messageType);
        return driver.decode(header, packet.payload());
    }
    
    @Override
    public Packet encode(Message message) {
        MessageType messageType = MessageType.ofMessage(message);
        TranslatorDriver driver = drivers.get(messageType);
        return driver.encode(message);
    }

}
