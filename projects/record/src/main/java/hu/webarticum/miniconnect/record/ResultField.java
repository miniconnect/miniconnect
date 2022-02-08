package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.record.converter.Converter;

public class ResultField {
    
    private final MiniValue value;

    private final Class<?> clazz;

    private final Object interpretedValue;

    private final Converter converter;
    

    public ResultField(
            MiniValue value, Class<?> clazz, Object interpretedValue, Converter converter) {
        this.value = value;
        this.clazz = clazz;
        this.interpretedValue = interpretedValue;
        this.converter = converter;
    }

    
    public MiniValue value() {
        return value;
    }
    
    public Class<?> clazz() {
        return clazz;
    }
    
    public Object get() {
        return interpretedValue;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> clazz) {
        if (clazz == this.clazz || clazz == Object.class) {
            return (T) interpretedValue;
        }
        
        return (T) converter.convert(interpretedValue, clazz);
    }
    
}
