package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;

// FIXME move to messenger?
public enum MessageType {

    // TODO
    
    QUERY_REQUEST(QueryRequest.class),

    // TODO
    
    ;
    
    private final Class<? extends Message> messageClazz;
    
    
    private MessageType(Class<? extends Message> messageClazz) {
        this.messageClazz = messageClazz;
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
    
    
    public Class<? extends Message> messageClazz() {
        return messageClazz;
    }
    
}
