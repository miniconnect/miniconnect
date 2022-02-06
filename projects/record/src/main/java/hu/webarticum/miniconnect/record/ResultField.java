package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;

// TODO: value interpreter
public class ResultField {
    
    private final MiniValue value;
    

    private ResultField(MiniValue value) {
        this.value = value;
    }

    public static ResultField of(MiniValue value) {
        return new ResultField(value);
    }
    
    
    public MiniValue value() {
        return value;
    }
    
    // TODO
    // TODO: as(...) asInt() etc.
    
}
