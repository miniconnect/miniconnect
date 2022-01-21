package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.transfer.Packet;

@FunctionalInterface
public interface MessageDecoder {

    public Message decode(Packet packet);
    
}
