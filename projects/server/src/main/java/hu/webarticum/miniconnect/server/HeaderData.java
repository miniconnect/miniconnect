package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public class HeaderData {

    private final MessageType messageType;
    
    private final long sessionId;
    
    private final int exchangeId;
    
    
    private HeaderData(MessageType messageType, long sessionId, int exchangeId) {
        this.messageType = messageType;
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
    }
    
    public static HeaderData of(MessageType messageType, long sessionId, int exchangeId) {
        return new HeaderData(messageType, sessionId, exchangeId);
    }

    public static HeaderData ofMessage(Message message) {
        MessageType messageType = MessageType.ofMessage(message);
        long sessionId = 0L;
        int exchangeId = 0;
        if (message instanceof SessionMessage) {
            sessionId = ((SessionMessage) message).sessionId();
            if (message instanceof ExchangeMessage) {
                exchangeId = ((ExchangeMessage) message).exchangeId();
            }
        }
        return HeaderData.of(messageType, sessionId, exchangeId);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public long getSessionId() {
        return sessionId;
    }

    public int getExchangeId() {
        return exchangeId;
    }

}
