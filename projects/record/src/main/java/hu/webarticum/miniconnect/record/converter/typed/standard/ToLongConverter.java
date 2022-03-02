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
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).toEpochDay();
        } else if (source instanceof LocalTime) {
            return (long) ((LocalTime) source).toSecondOfDay();
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof Instant) {
            return ((Instant) source).getEpochSecond();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).toBigInteger().longValueExact();
        }
    }

}
