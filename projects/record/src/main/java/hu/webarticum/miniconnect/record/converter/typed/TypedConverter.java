package hu.webarticum.miniconnect.record.converter.typed;

import java.util.ArrayList;
import java.util.Collection;

public interface TypedConverter<T> {

    public Class<T> targetClazz();
    
    public T convert(Object source);
    
    
    public static Collection<TypedConverter<?>> defaultConverters() {
        
        // TODO
        return new ArrayList<>();
        
    }
    
}
