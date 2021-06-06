package hu.webarticum.miniconnect.api;

// FIXME: rename to MiniValueInterpreter?
public interface MiniValueEncoder {

    public MiniColumnHeader headerFor(String columnName);
    
    public MiniValue encode(Object value);
    
    public Object decode(MiniValue value);
    
}
