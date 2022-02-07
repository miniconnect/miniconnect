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
    
    public boolean asBoolean() {
        
        // TODO: eliminate autoboxing
        return as(Boolean.class);
        
    }
    
    public byte asByte() {

        // TODO: eliminate autoboxing
        return as(Byte.class);
        
    }
    
    public char asChar() {

        // TODO: eliminate autoboxing
        return as(Character.class);
        
    }
    
    public short asShort() {

        // TODO: eliminate autoboxing
        return as(Short.class);
        
    }
    
    public int asInt() {

        // TODO: eliminate autoboxing
        return as(Integer.class);
        
    }
    
    public long asLong() {

        // TODO: eliminate autoboxing
        return as(Long.class);
        
    }
    
    public float asFloat() {

        // TODO: eliminate autoboxing
        return as(Float.class);
        
    }
    
    public double asDouble() {

        // TODO: eliminate autoboxing
        return as(Double.class);
        
    }
    
}
