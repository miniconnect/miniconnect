package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
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
import hu.webarticum.miniconnect.record.util.Numbers;

public class ToLocalTimeConverter implements TypedConverter<LocalTime> {

    @Override
    public Class<LocalTime> targetClazz() {
        return LocalTime.class;
    }

    @Override
    public LocalTime convert(Object source) {
        if (source instanceof LocalTime) {
            return (LocalTime) source;
        } else if (source instanceof OffsetTime) {
            return ((OffsetTime) source).toLocalTime();
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toLocalTime();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toLocalTime();
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toLocalTime();
        } else if (source instanceof Timestamp) {
            return ((Timestamp) source).toLocalDateTime().toLocalTime();
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC).toLocalTime();
        } else if (source instanceof Number) {
            BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
            long nanosOfDay= bigDecimalValue.unscaledValue().longValue();
            return LocalTime.ofNanoOfDay(nanosOfDay);
        } else if (source instanceof String) {
            String timeString = (String) source;
            if (timeString.indexOf('Z', 5) >= 0 || timeString.indexOf('+', 5) >= 0 || timeString.indexOf('-', 5) >= 0) {
                return OffsetTime.parse(timeString).toLocalTime();
            } else {
                return LocalTime.parse(timeString);
            }
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source).toLocalTime();
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
