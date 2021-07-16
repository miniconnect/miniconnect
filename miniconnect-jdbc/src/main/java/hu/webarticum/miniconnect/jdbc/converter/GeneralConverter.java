package hu.webarticum.miniconnect.jdbc.converter;

import java.util.HashMap;
import java.util.Map;

public class GeneralConverter {

    private final Map<Class<?>, SpecificConverter<?>> specificConverters = new HashMap<>();
    
    
    public GeneralConverter() {
        specificConverters.put(Boolean.class, new BooleanConverter());
        specificConverters.put(Byte.class, new ByteConverter());
        specificConverters.put(Character.class, new CharacterConverter());
        specificConverters.put(Short.class, new ShortConverter());
        specificConverters.put(Integer.class, new IntegerConverter());
        specificConverters.put(Long.class, new LongConverter());
        specificConverters.put(Float.class, new FloatConverter());
        specificConverters.put(Double.class, new DoubleConverter());
        
        // TODO
        
    }
    
    
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> targetType, Object modifier) {
        if (value == null) {
            return null;
        } else if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (targetType == String.class) {
            return (T) value.toString();
        } else if (specificConverters.containsKey(targetType)) {
            SpecificConverter<T> specificConverter =
                    (SpecificConverter<T>) specificConverters.get(targetType);
            return specificConverter.convert(value, modifier);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Conversion to %s is not supported",
                    targetType));
        }
    }
    
}
