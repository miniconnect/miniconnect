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

public class ToInstantConverter implements TypedConverter<Instant> {

    @Override
    public Class<Instant> targetClazz() {
        return Instant.class;
    }

    @Override
    public Instant convert(Object source) {
        if (source instanceof Instant) {
            return (Instant) source;
        } else if (source instanceof Timestamp) {
            return ((Timestamp) source).toInstant();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toInstant();
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).toInstant();
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toInstant(ZoneOffset.UTC);
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay(ZoneOffset.UTC).toInstant();
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).atDate(LocalDate.ofEpochDay(0)).toInstant(ZoneOffset.UTC);
        } else if (source instanceof OffsetTime) {
            return ((OffsetTime) source).atDate(LocalDate.ofEpochDay(0)).toInstant();
        } else if (source instanceof ZoneOffset) {
            return LocalDate.ofEpochDay(0).atStartOfDay((ZoneOffset) source).toInstant();
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay(ZoneOffset.UTC).plus((TemporalAmount) source).toInstant();
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return Instant.ofEpochSecond(((Number) source).longValue());
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
                int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
                return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
            }
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            long secondsSinceEpoch = reader.readLong();
            int nanoOfSecond = reader.readInt();
            return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return convert(Temporals.parse((String) source));
        } else if (source instanceof ClobValue) {
            return convert(Temporals.parse(((ClobValue) source).toString()));
        } else if (source instanceof Boolean) {
            return Instant.ofEpochSecond((Boolean) source ? 1 : 0);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
