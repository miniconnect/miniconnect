package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToLocalDateConverter implements TypedConverter<LocalDate> {

    @Override
    public Class<LocalDate> targetClazz() {
        return LocalDate.class;
    }

    @Override
    public LocalDate convert(Object source) {
        if (source instanceof LocalDate) {
            return (LocalDate) source;
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toLocalDate();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toLocalDate();
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC).toLocalDate();
        } else if (source instanceof Number) {
            return LocalDate.ofEpochDay(((Number) source).longValue());
        } else if (source instanceof String) {
            return LocalDate.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
