package hu.webarticum.miniconnect.record.converter.typed;

import java.math.BigDecimal;

import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToLongConverter implements TypedConverter<Long> {
    
    @Override
    public Class<Long> targetClazz() {
        return Long.class;
    }

    @Override
    public Long convert(Object source) {
        if (source instanceof Long) {
            return ((Long) source);
        } else if (source instanceof Number) {
            return ((Number) source).longValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1L : 0L;
        } else if (source instanceof Character) {
            return ((long) (char) source);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).toBigInteger().longValueExact();
        }
    }

}
