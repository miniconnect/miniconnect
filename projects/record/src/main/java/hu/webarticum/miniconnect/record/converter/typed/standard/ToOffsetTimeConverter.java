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
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toOffsetDateTime().toOffsetTime();
        } else if (source instanceof LocalDate) {
            return LocalTime.MIN.atOffset(ZoneOffset.UTC);
        } else if (source instanceof Timestamp) {
            return ((Timestamp) source).toInstant().atOffset(ZoneOffset.UTC).toOffsetTime();
        } else if (source instanceof Instant) {
            return ((Instant) source).atOffset(ZoneOffset.UTC).toOffsetTime();
        } else if (source instanceof ZoneOffset) {
            return LocalTime.MIN.atOffset(ZoneOffset.UTC);
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().atOffset(ZoneOffset.UTC).plus((TemporalAmount) source).toOffsetTime();
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return LocalTime.ofSecondOfDay(((Number) source).longValue()).atOffset(ZoneOffset.UTC);
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long nanosOfDay= bigDecimalValue.unscaledValue().longValue();
                return LocalTime.ofNanoOfDay(nanosOfDay).atOffset(ZoneOffset.UTC);
            }
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            long nanoOfDay = reader.readLong();
            int offsetSeconds = reader.readInt();
            return OffsetTime.of(LocalTime.ofNanoOfDay(nanoOfDay), ZoneOffset.ofTotalSeconds(offsetSeconds));
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return OffsetTime.parse((String) source);
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source).atOffset(ZoneOffset.UTC).toOffsetTime();
        } else if (source instanceof Boolean) {
            return LocalTime.ofSecondOfDay((Boolean) source ? 1 : 0).atOffset(ZoneOffset.UTC);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
