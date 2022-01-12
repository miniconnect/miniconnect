package hu.webarticum.miniconnect.protocol.old;

import hu.webarticum.miniconnect.util.data.ByteString;

public interface Message {

    public ByteString encode();
    
}
