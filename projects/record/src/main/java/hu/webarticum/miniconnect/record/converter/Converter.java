package hu.webarticum.miniconnect.record.converter;

@FunctionalInterface
public interface Converter {

    public Object convert(Object source, Class<?> targetClazz);
    
}
