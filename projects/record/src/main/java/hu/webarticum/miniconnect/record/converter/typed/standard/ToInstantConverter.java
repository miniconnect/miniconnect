package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToInstantConverter implements TypedConverter<Instant> {

    @Override
    public Class<Instant> targetClazz() {
        return Instant.class;
    }

    @Override
    public Instant convert(Object source) {
        if (source instanceof Instant) {
            return (Instant) source;
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toInstant(ZoneOffset.UTC);
        } else if (source instanceof LocalDate) {
            return LocalDateTime.of(
                    (LocalDate) source,
                    LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC);
        } else if (source instanceof Number) {
            return Instant.ofEpochSecond(((Number) source).longValue());
        } else if (source instanceof String) {
            return Instant.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
