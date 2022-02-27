package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToLocalDateTimeConverter implements TypedConverter<LocalDateTime> {

    @Override
    public Class<LocalDateTime> targetClazz() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime convert(Object source) {
        if (source instanceof LocalDateTime) {
            return (LocalDateTime) source;
        } else if (source instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) source, LocalTime.of(0, 0));
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC);
        } else if (source instanceof Number) {
            return LocalDateTime.ofEpochSecond(((Number) source).longValue(), 0, ZoneOffset.UTC);
        } else if (source instanceof String) {
            return LocalDateTime.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
