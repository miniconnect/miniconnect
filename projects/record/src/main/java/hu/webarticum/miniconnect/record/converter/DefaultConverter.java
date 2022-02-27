package hu.webarticum.miniconnect.record.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class DefaultConverter implements Converter {
    
    private final Map<Class<?>, TypedConverter<?>> typedConvertersByClazz;
    
    private Object cachedObjectMapper = null;
    

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
        if (typedConverter != null) {
            return typedConverter.convert(source);
        }
        
        if (source instanceof CustomValue) {
            return tryToConvertWithJackson(source, targetClazz);
        }
        
        throw new UnsupportedConversionException(
                "No converter for target class: " + targetClazz,
                source,
                targetClazz);
    }

    private synchronized Object tryToConvertWithJackson(Object source, Class<?> targetClazz) {
        try {
            Object objectMapper = requireJacksonObjectMapper();
            return invokeConvertValue(objectMapper, source, targetClazz);
        } catch (
                ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException |
                NoSuchMethodException |
                SecurityException e) {
            throw new UnsupportedConversionException(
                    "Mapping with ObjectMapper failed",
                    source,
                    targetClazz,
                    e);
        }
    }
    
    private synchronized Object requireJacksonObjectMapper() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchMethodException,
            SecurityException {
        if (cachedObjectMapper == null) {
            loadJacksonObjectMapper();
        }
        return cachedObjectMapper;
    }

    private synchronized void loadJacksonObjectMapper() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchMethodException,
            SecurityException {
        Class<?> clazz = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        cachedObjectMapper = clazz.getDeclaredConstructor().newInstance();
    }
    
    private Object invokeConvertValue(
            Object objectMapper, Object source, Class<?> targetClazz) throws
            NoSuchMethodException,
            SecurityException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException {
        Class<?> clazz = objectMapper.getClass();
        Method method = clazz.getMethod("convertValue", Object.class, Class.class);
        return method.invoke(objectMapper, source, targetClazz);
    }
    
}
