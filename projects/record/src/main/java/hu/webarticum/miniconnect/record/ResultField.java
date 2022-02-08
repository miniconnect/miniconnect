package hu.webarticum.miniconnect.record;

import java.util.function.BiFunction;

import hu.webarticum.miniconnect.api.MiniValue;

public class ResultField {
    
    private final MiniValue value;

    private final Class<?> clazz;

    private final Object interpretedValue;

    private final BiFunction<Object, Class<?>, Object> converter;
    

    public ResultField(
            MiniValue value,
            Class<?> clazz,
            Object interpretedValue,
            BiFunction<Object, Class<?>, Object> converter) {
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
        
        return (T) converter.apply(interpretedValue, clazz);
    }
    
}
