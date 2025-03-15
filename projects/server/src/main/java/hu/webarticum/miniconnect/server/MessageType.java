package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionCloseRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionCloseResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;

public enum MessageType {

    SESSION_INIT_REQUEST('I', SessionInitRequest.class),

    QUERY_REQUEST('Q', QueryRequest.class),
    
    LARGE_DATA_HEAD_REQUEST('L', LargeDataHeadRequest.class),
    
    LARGE_DATA_PART_REQUEST('D', LargeDataPartRequest.class),
    
    SESSION_CLOSE_REQUEST('C', SessionCloseRequest.class),
    

    SESSION_INIT_RESPONSE('i', SessionInitResponse.class),

    RESULT_RESPONSE('q', ResultResponse.class),

    RESULT_SET_ROWS_RESPONSE('r', ResultSetRowsResponse.class),

    RESULT_SET_VALUE_PART_RESPONSE('v', ResultSetValuePartResponse.class),

    RESULT_SET_EOF_RESPONSE('f', ResultSetEofResponse.class),

    LARGE_DATA_SAVE_RESPONSE('l', LargeDataSaveResponse.class),
    
    SESSION_CLOSE_RESPONSE('c', SessionCloseResponse.class),

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
