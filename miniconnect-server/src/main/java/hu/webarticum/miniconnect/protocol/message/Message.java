package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.transfer.util.ByteString;

public interface Message {

    public ByteString encode();
    
}
