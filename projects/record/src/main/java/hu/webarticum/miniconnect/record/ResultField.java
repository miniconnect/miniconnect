package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;

// FIXME: how to handle primitive types (should we)?
public class ResultField {
    
    private final MiniValue value;

    private final Object valueInterpreter;
    

    public ResultField(MiniValue value) {
        this(value, new Object());
    }
    
    public ResultField(MiniValue value, Object valueInterpreter) {
        this.value = value;
        this.valueInterpreter = valueInterpreter;
    }

    
    public MiniValue value() {
        return value;
    }
    
    public <T> T as(Class<T> clazz) {
        
        // TODO
        return null;
        
    }
    
}
