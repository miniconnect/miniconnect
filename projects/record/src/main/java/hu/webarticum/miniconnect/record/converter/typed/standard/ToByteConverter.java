package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToByteConverter implements TypedConverter<Byte> {
    
    @Override
    public Class<Byte> targetClazz() {
        return Byte.class;
    }

    @Override
    public Byte convert(Object source) {
        if (source instanceof Byte) {
            return (Byte) source;
        } else if (source instanceof Number) {
            return ((Number) source).byteValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? (byte) 1 : (byte) 0;
        } else if (source instanceof Character) {
            return ((byte) (char) source);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).toBigInteger().byteValueExact();
        }
    }

}
