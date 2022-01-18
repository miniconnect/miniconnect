package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;

public enum MessageType {

    // TODO: start/close session
    // FIXME: sending out session ids?

    QUERY_REQUEST('Q', QueryRequest.class),
    
    LARGE_DATA_HEAD_REQUEST('L', LargeDataHeadRequest.class),
    
    LARGE_DATA_PART_REQUEST('D', LargeDataPartRequest.class),

    RESULT_RESPONSE('q', ResultResponse.class),

    RESULT_SET_ROWS_RESPONSE('r', ResultSetRowsResponse.class),

    RESULT_SET_VALUE_PART_RESPONSE('v', ResultSetValuePartResponse.class),

    RESULT_SET_EOF_RESPONSE('f', ResultSetEofResponse.class),

    LARGE_DATA_SAVE_RESPONSE('l', LargeDataSaveResponse.class),

    ;
    
    
    private final char symbol;
    
    private final Class<? extends Message> messageClazz;
    

    private MessageType(char symbol, Class<? extends Message> messageClazz) {
        this.symbol = symbol;
        this.messageClazz = messageClazz;
    }
    
    public static MessageType ofSymbol(char symbol) {
        for (MessageType messageType : values()) {
            if (messageType.symbol == symbol) {
                return messageType;
            }
        }
        
        throw new IllegalArgumentException("No message type for symbol: " + symbol);
    }
    
    public static MessageType ofMessage(Message message) {
        Class<? extends Message> clazzToCheck = message.getClass();
        for (MessageType messageType : values()) {
            if (messageType.messageClazz == clazzToCheck) {
                return messageType;
            }
        }
        
        throw new IllegalArgumentException("No message type for class: " + clazzToCheck);
    }
    

    public char symbol() {
        return symbol;
    }
    
    public Class<? extends Message> messageClazz() {
        return messageClazz;
    }
    
}
