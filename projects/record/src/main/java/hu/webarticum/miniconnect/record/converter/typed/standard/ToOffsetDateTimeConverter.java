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

public class ToOffsetDateTimeConverter implements TypedConverter<OffsetDateTime> {

    @Override
    public Class<OffsetDateTime> targetClazz() {
        return OffsetDateTime.class;
    }

    @Override
    public OffsetDateTime convert(Object source) {
        if (source instanceof OffsetDateTime) {
            return (OffsetDateTime) source;
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toOffsetDateTime();
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        } else if (source instanceof Timestamp) {
            return ((Timestamp) source).toInstant().atOffset(ZoneOffset.UTC);
        } else if (source instanceof Instant) {
            return ((Instant) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof OffsetTime) {
            return LocalDate.ofEpochDay(0).atTime((OffsetTime) source);
        } else if (source instanceof LocalTime) {
            return LocalDate.ofEpochDay(0).atTime((LocalTime) source).atOffset(ZoneOffset.UTC);
        } else if (source instanceof ZoneOffset) {
            return LocalDate.ofEpochDay(0).atStartOfDay().atOffset(ZoneOffset.UTC);
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().atOffset(ZoneOffset.UTC).plus((TemporalAmount) source);
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return Instant.ofEpochSecond(((Number) source).longValue()).atOffset(ZoneOffset.UTC);
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
                int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
                return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond).atOffset(ZoneOffset.UTC);
            }
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            long daysSinceEpoch = reader.readLong();
            long nanoOfDay = reader.readLong();
            int offsetSeconds = reader.readInt();
            LocalDateTime localDateTimeValue = LocalDateTime.of(
                    LocalDate.ofEpochDay(daysSinceEpoch),
                    LocalTime.ofNanoOfDay(nanoOfDay));
            return OffsetDateTime.of(localDateTimeValue, ZoneOffset.ofTotalSeconds(offsetSeconds));
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return OffsetDateTime.parse((String) source);
        } else if (source instanceof Boolean) {
            return Instant.ofEpochSecond((Boolean) source ? 1 : 0).atOffset(ZoneOffset.UTC);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
