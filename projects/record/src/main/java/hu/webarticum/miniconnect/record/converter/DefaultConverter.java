package hu.webarticum.miniconnect.record.converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class DefaultConverter implements Converter {
    
    private final Map<Class<?>, TypedConverter<?>> typedConvertersByClazz;
    

    public DefaultConverter() {
        this(TypedConverter.defaultConverters());
    }
    
    public DefaultConverter(Collection<TypedConverter<?>> typedConverters) {
        typedConvertersByClazz = new HashMap<>(typedConverters.size());
        for (TypedConverter<?> typedConverter : typedConverters) {
            typedConvertersByClazz.put(typedConverter.targetClazz(), typedConverter);
        }
    }
    
    
    @Override
    public Object convert(Object source, Class<?> targetClazz) {
        if (source == null) {
            return null;
        }
        
        if (targetClazz.isInstance(source)) {
            return source;
        }
        
        TypedConverter<?> typedConverter = typedConvertersByClazz.get(targetClazz);
        if (typedConverter == null) {
            throw new UnsupportedConversionException(
                    "No converter for target class: " + targetClazz,
                    source,
                    targetClazz);
        }
        
        return typedConverter.convert(source);
    }

}
