package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;
import hu.webarticum.miniconnect.record.util.Numbers;
import hu.webarticum.miniconnect.record.util.Temporals;

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
        } else if (source instanceof LocalDate) {
            return LocalTime.MIN;
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC).toLocalTime();
        } else if (source instanceof ZoneOffset) {
            return LocalTime.MIN;
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source).toLocalTime();
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return LocalTime.ofSecondOfDay(((Number) source).longValue());
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long nanosOfDay = bigDecimalValue.unscaledValue().longValue();
                return LocalTime.ofNanoOfDay(nanosOfDay);
            }
        } else if (source instanceof ByteString) {
            return LocalTime.ofNanoOfDay(((ByteString) source).reader().readLong());
        } else if (source instanceof BlobValue) {
            return LocalTime.ofNanoOfDay(((BlobValue) source).contentAccess().get().reader().readLong());
        } else if (source instanceof String) {
            return convert(Temporals.parse((String) source));
        } else if (source instanceof ClobValue) {
            return convert(Temporals.parse(((ClobValue) source).toString()));
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source).toLocalTime();
        } else if (source instanceof Boolean) {
            return LocalTime.ofSecondOfDay((Boolean) source ? 1 : 0);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
