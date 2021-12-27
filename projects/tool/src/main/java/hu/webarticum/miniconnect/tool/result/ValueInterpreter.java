package hu.webarticum.miniconnect.tool.result;

import hu.webarticum.miniconnect.api.MiniValue;

public interface ValueInterpreter {

    public MiniValue encode(Object value);
    
    public Object decode(MiniValue value);
    
}
