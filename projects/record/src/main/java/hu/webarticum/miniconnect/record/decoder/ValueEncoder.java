package hu.webarticum.miniconnect.record.decoder;

import hu.webarticum.miniconnect.api.MiniValue;

public interface ValueEncoder {

    public MiniValue encode(Object value);
    
}
