package hu.webarticum.miniconnect.record.converter.typed;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToByteStringConverter implements TypedConverter<ByteString> {
    
    @Override
    public Class<ByteString> targetClazz() {
        return ByteString.class;
    }

    @Override
    public ByteString convert(Object source) {
        if (source instanceof ByteString) {
            return (ByteString) source;
        } else if (source instanceof String) {
            return ByteString.of((String) source);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        }
        
        Class<?> sourceClazz = source.getClass();
        if (sourceClazz.isArray()) {
            Class<?> sourceComponentClazz = sourceClazz.getComponentType();
            if (sourceComponentClazz == byte.class) {
                return ByteString.of((byte[]) source);
            } else if (sourceComponentClazz == char.class) {
                return ByteString.of(new String((char[]) source));
            }
        }
        
        return ByteString.of(source.toString());
    }

}
