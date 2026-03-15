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

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;
import hu.webarticum.miniconnect.record.util.Numbers;
import hu.webarticum.miniconnect.record.util.Temporals;

public class ToLocalDateTimeConverter implements TypedConverter<LocalDateTime> {

    @Override
    public Class<LocalDateTime> targetClazz() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime convert(Object source) {
        if (source instanceof LocalDateTime) {
            return (LocalDateTime) source;
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toLocalDateTime();
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toLocalDateTime();
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toLocalDateTime());
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC);
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).atDate(LocalDate.ofEpochDay(0));
        } else if (source instanceof OffsetTime) {
            return ((OffsetTime) source).toLocalTime().atDate(LocalDate.ofEpochDay(0));
        } else if (source instanceof ZoneOffset) {
            return LocalDate.ofEpochDay(0).atStartOfDay();
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay().plus((TemporalAmount) source);
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return Instant.ofEpochSecond(((Number) source).longValue()).atOffset(ZoneOffset.UTC).toLocalDateTime();
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
                int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
                Instant instantValue = Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
                return instantValue.atOffset(ZoneOffset.UTC).toLocalDateTime();
            }
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            long daysSinceEpoch = reader.readLong();
            long nanoOfDay = reader.readLong();
            return LocalDateTime.of(LocalDate.ofEpochDay(daysSinceEpoch), LocalTime.ofNanoOfDay(nanoOfDay));
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return convert(Temporals.parse((String) source));
        } else if (source instanceof ClobValue) {
            return convert(Temporals.parse(((ClobValue) source).toString()));
        } else if (source instanceof Boolean) {
            return LocalDate.ofEpochDay(0).atTime(LocalTime.ofSecondOfDay((Boolean) source ? 1 : 0));
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
