package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.record.converter.Converter;

public class ResultField {
    
    private final MiniValue value;

    private final Object interpretedValue;

    private final Converter converter;
    

    public ResultField(
            MiniValue value, Object interpretedValue, Converter converter) {
        this.value = value;
        this.interpretedValue = interpretedValue;
        this.converter = converter;
    }

    
    public MiniValue value() {
        return value;
    }
    
    public boolean isNull() {
        return interpretedValue == null;
    }
    
    public Object get() {
        return interpretedValue;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> clazz) {
        return (T) converter.convert(interpretedValue, clazz);
    }
    
}
