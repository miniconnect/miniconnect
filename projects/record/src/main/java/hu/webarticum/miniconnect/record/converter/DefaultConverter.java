package hu.webarticum.miniconnect.record.converter;

public class DefaultConverter implements Converter {

    // TODO
    @Override
    public Object convert(Object source, Class<?> targetClazz) {
        if (source == null) {
            return null;
        }
        
        if (!targetClazz.isInstance(source)) {
            throw new IllegalArgumentException(
                    "Non-ancestor conversion is not supported currently");
        }
        
        return source;
    }

}
