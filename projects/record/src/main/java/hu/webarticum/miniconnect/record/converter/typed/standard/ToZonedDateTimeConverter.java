package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
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
        } else if (source instanceof OffsetTime) {
            return LocalDate.ofEpochDay(0).atTime((OffsetTime) source).toZonedDateTime();
        } else if (source instanceof LocalTime) {
            return LocalDate.ofEpochDay(0).atTime((LocalTime) source).atZone(ZoneOffset.UTC);
        } else if (source instanceof ZoneOffset) {
            return LocalDate.ofEpochDay(0).atStartOfDay((ZoneOffset) source);
        } else if (source instanceof TemporalAmount) {
            return LocalDate.ofEpochDay(0).atStartOfDay(ZoneOffset.UTC).plus((TemporalAmount) source);
        } else if (source instanceof Number) {
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                return Instant.ofEpochSecond(((Number) source).longValue()).atZone(ZoneOffset.UTC);
            } else {
                BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
                long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
                int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
                return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond).atZone(ZoneOffset.UTC);
            }
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            long daysSinceEpoch = reader.readLong();
            long nanoOfDay = reader.readLong();
            int offsetSeconds = reader.readInt();
            String zoneName = new String(reader.readRemaining(), StandardCharsets.UTF_8);
            LocalDateTime localDateTimeValue = LocalDateTime.of(
                    LocalDate.ofEpochDay(daysSinceEpoch),
                    LocalTime.ofNanoOfDay(nanoOfDay));
            ZoneOffset offsetValue = ZoneOffset.ofTotalSeconds(offsetSeconds);
            ZoneId zoneIdValue = ZoneId.of(zoneName);
            return ZonedDateTime.ofLocal(localDateTimeValue, zoneIdValue, offsetValue);
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return convert(Temporals.parse((String) source));
        } else if (source instanceof ClobValue) {
            return convert(Temporals.parse(((ClobValue) source).toString()));
        } else if (source instanceof Boolean) {
            return Instant.ofEpochSecond((Boolean) source ? 1 : 0).atZone(ZoneOffset.UTC);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
