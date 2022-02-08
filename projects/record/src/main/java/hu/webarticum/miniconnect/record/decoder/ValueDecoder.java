package hu.webarticum.miniconnect.record.decoder;

import hu.webarticum.miniconnect.api.MiniValue;

public interface ValueDecoder {

    public Object decode(MiniValue value);
    
}
