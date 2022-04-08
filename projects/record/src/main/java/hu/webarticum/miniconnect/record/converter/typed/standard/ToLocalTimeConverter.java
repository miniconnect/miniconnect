package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

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
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC).toLocalTime();
        } else if (source instanceof Number) {
            BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
            long nanosOfDay= bigDecimalValue.unscaledValue().longValue();
            return LocalTime.ofNanoOfDay(nanosOfDay);
        } else if (source instanceof String) {
            return LocalTime.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
