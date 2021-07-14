package hu.webarticum.miniconnect.jdbc.converter;

public interface SpecificConverter<T> {

    public T convert(Object value, Object modifier);
    
}
