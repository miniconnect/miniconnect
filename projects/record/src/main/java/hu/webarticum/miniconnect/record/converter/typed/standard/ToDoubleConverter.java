package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToDoubleConverter implements TypedConverter<Double> {
    
    @Override
    public Class<Double> targetClazz() {
        return Double.class;
    }

    @Override
    public Double convert(Object source) {
        if (source instanceof Double) {
            return ((Double) source);
        } else if (source instanceof Number) {
            return ((Number) source).doubleValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1d: 0d;
        } else if (source instanceof Character) {
            return ((double) (char) source);
        } else if (source instanceof LocalDate) {
            return (double) ((LocalDate) source).toEpochDay();
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).toNanoOfDay() / 1_000_000_000d;
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            long secondsSinceEpoch = ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
            double fragmentOfSecond = ((LocalDateTime) source).getNano() / 1_000_000_000d;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof Instant) {
            long secondsSinceEpoch = ((Instant) source).getEpochSecond();
            double fragmentOfSecond = ((Instant) source).getNano() / 1_000_000_000d;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).doubleValue();
        }
    }

}
