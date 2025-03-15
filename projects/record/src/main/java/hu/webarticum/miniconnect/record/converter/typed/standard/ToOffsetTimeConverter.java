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

public class ToOffsetTimeConverter implements TypedConverter<OffsetTime> {

    @Override
    public Class<OffsetTime> targetClazz() {
        return OffsetTime.class;
    }

    @Override
    public OffsetTime convert(Object source) {
        if (source instanceof OffsetTime) {
            return (OffsetTime) source;
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toLocalTime().atOffset(ZoneOffset.UTC);
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toOffsetTime();
        } else if (source instanceof Instant) {
            return ((Instant) source).atOffset(ZoneOffset.UTC).toOffsetTime();
        } else if (source instanceof Number) {
            BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
            long nanosOfDay= bigDecimalValue.unscaledValue().longValue();
            return LocalTime.ofNanoOfDay(nanosOfDay).atOffset(ZoneOffset.UTC);
        } else if (source instanceof String) {
            return OffsetTime.parse((String) source);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
