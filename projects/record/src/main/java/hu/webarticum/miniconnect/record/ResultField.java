package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;

// TODO: value interpreter
public class ResultField {
    
    private final MiniValue value;

    // TODO
    private final Object valueInterpreter;
    

    public ResultField(MiniValue value, Object valueInterpreter) {
        this.value = value;
        this.valueInterpreter = valueInterpreter;
    }

    
    public MiniValue value() {
        return value;
    }
    
    // TODO
    // TODO: as(...) asInt() etc.
    
}
