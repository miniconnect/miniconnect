package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;

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
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toLocalDate();
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toLocalDateTime().toLocalDate());
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC).toLocalDate();
        } else if (source instanceof LocalTime) {
            return LocalDate.ofEpochDay(0);
        } else if (source instanceof OffsetTime) {
            return LocalDate.ofEpochDay(0);
        } else if (source instanceof Number) {
            return LocalDate.ofEpochDay(((Number) source).longValue());
        } else if (source instanceof String) {
            return LocalDate.parse((String) source);
        } else if (source instanceof TemporalAmount) {
            return LocalDateTime.MIN.plus((TemporalAmount) source).toLocalDate();
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
