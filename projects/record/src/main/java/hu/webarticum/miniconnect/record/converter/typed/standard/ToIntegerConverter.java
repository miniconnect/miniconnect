package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.LocalTime;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToIntegerConverter implements TypedConverter<Integer> {
    
    @Override
    public Class<Integer> targetClazz() {
        return Integer.class;
    }

    @Override
    public Integer convert(Object source) {
        if (source instanceof Integer) {
            return ((Integer) source);
        } else if (source instanceof Number) {
            return ((Number) source).intValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1 : 0;
        } else if (source instanceof Character) {
            return ((int) (char) source);
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).getSecond();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).toBigInteger().intValueExact();
        }
    }

}
