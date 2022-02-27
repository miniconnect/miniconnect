package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

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
        } else if (source instanceof LocalDateTime) {
            long secondsSinceEpoch = ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
            float fragmentOfSecond = ((LocalDateTime) source).getNano() / 1_000_000_000f;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof Instant) {
            long secondsSinceEpoch = ((Instant) source).getEpochSecond();
            float fragmentOfSecond = ((Instant) source).getNano() / 1_000_000_000f;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).floatValue();
        }
    }

}
