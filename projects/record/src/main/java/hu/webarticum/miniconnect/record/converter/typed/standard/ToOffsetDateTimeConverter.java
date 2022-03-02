package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToOffsetDateTimeConverter implements TypedConverter<OffsetDateTime> {

    @Override
    public Class<OffsetDateTime> targetClazz() {
        return OffsetDateTime.class;
    }

    @Override
    public OffsetDateTime convert(Object source) {
        if (source instanceof OffsetDateTime) {
            return (OffsetDateTime) source;
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay().atOffset(ZoneOffset.UTC);
        } else if (source instanceof Instant) {
            return ((Instant) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof Number) {
            return Instant.ofEpochSecond(((Number) source).longValue()).atOffset(ZoneOffset.UTC);
        } else if (source instanceof String) {
            return OffsetDateTime.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
