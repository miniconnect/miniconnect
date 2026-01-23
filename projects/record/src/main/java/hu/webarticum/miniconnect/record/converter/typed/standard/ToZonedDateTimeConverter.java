package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.util.Numbers;

public class ToZonedDateTimeConverter implements TypedConverter<ZonedDateTime> {

    @Override
    public Class<ZonedDateTime> targetClazz() {
        return ZonedDateTime.class;
    }

    @Override
    public ZonedDateTime convert(Object source) {
        if (source instanceof ZonedDateTime) {
            return (ZonedDateTime) source;
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).atZoneSameInstant(ZoneOffset.UTC);
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).atZone(ZoneOffset.UTC);
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay(ZoneOffset.UTC);
        } else if (source instanceof Timestamp) {
            return ((Timestamp) source).toInstant().atZone(ZoneOffset.UTC);
        } else if (source instanceof Instant) {
            return ((Instant) source).atZone(ZoneOffset.UTC);
        } else if (source instanceof Number) {
            BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
            long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
            int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
            return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond).atZone(ZoneOffset.UTC);
        } else if (source instanceof String) {
            return ZonedDateTime.parse((String) source);
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source).atZone(ZoneOffset.UTC);
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
