package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.transfer.Packet;

@FunctionalInterface
public interface MessageEncoder {

    public Packet encode(Message message);
    
}
