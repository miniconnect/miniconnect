package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public interface Message {

    public ByteString encode();
    
}
