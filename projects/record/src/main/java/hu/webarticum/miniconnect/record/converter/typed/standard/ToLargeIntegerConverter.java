package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class ToLargeIntegerConverter implements TypedConverter<LargeInteger> {

    @Override
    public Class<LargeInteger> targetClazz() {
        return LargeInteger.class;
    }

    @Override
    public LargeInteger convert(Object source) {
        if (source instanceof LargeInteger) {
            return (LargeInteger) source;
        } else if (source instanceof BigInteger) {
            return LargeInteger.of((BigInteger) source);
        } else if (source instanceof BigDecimal) {
            return LargeInteger.of(((BigDecimal) source).toBigInteger());
        } else if (source instanceof Number) {
            return LargeInteger.of(((Number) source).longValue());
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? LargeInteger.ONE: LargeInteger.ZERO;
        } else if (source instanceof ByteString) {
            return LargeInteger.of(((ByteString) source).extract());
        } else if (source instanceof BlobValue) {
            return LargeInteger.of(((BlobValue) source).contentAccess().get().extract());
        } else if (source instanceof Character) {
            return LargeInteger.of((long) (char) source); // NOSONAR it's better to be explicit
        } else if (source instanceof LocalDate) {
            return LargeInteger.of(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalTime) {
            return LargeInteger.of(((LocalTime) source).getSecond());
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            return LargeInteger.of(((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC));
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof ZonedDateTime) {
            return convert(((ZonedDateTime) source).toInstant());
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            return LargeInteger.of(((Instant) source).getEpochSecond());
        } else if (source instanceof ZoneOffset) {
            return LargeInteger.of(((ZoneOffset) source).getTotalSeconds());
        } else if (source instanceof DateTimeDelta) {
            return LargeInteger.of(((DateTimeDelta) source).toCollapsedDuration().getSeconds());
        } else if (source instanceof Duration) {
            return LargeInteger.of(((Duration) source).getSeconds());
        } else if (source instanceof Period) {
            Period period = (Period) source;
            return LargeInteger.of(period.getYears() * 365).add(period.getMonths() * 30).add(period.getDays());
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return LargeInteger.of(source.toString());
        }
    }

}
