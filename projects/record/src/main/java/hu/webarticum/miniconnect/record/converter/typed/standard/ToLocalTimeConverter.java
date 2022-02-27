package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToLocalTimeConverter implements TypedConverter<LocalTime> {

    @Override
    public Class<LocalTime> targetClazz() {
        return LocalTime.class;
    }

    @Override
    public LocalTime convert(Object source) {
        if (source instanceof LocalTime) {
            return (LocalTime) source;
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toLocalTime();
        } else if (source instanceof Instant) {
            return LocalTime.ofInstant((Instant) source, ZoneOffset.UTC);
        } else if (source instanceof Number) {
            return LocalTime.ofSecondOfDay(((Number) source).longValue());
        } else if (source instanceof String) {
            return LocalTime.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
