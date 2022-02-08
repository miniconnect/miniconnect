package hu.webarticum.miniconnect.record.interpreter;

import hu.webarticum.miniconnect.api.MiniValue;

// FIXME/TODO
public interface ValueInterpreter {

    public MiniValue encode(Object value);
    
    public Object decode(MiniValue value);
    
}
