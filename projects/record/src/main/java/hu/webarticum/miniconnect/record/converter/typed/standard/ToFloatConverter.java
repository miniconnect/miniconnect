package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToFloatConverter implements TypedConverter<Float> {
    
    @Override
    public Class<Float> targetClazz() {
        return Float.class;
    }

    @Override
    public Float convert(Object source) {
        if (source instanceof Float) {
            return ((Float) source);
        } else if (source instanceof Number) {
            return ((Number) source).floatValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1f : 0f;
        } else if (source instanceof Character) {
            return ((float) (char) source);
        } else if (source instanceof LocalDate) {
            return (float) ((LocalDate) source).toEpochDay();
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).toNanoOfDay() / 1_000_000_000f;
        } else if (source instanceof Instant) {
            return ((Instant) source).toEpochMilli() / 1_000f;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).floatValue();
        }
    }

}