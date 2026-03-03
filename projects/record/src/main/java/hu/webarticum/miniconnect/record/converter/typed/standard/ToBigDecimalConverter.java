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

public class ToBigDecimalConverter implements TypedConverter<BigDecimal> {

    @Override
    public Class<BigDecimal> targetClazz() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal convert(Object source) {
        if (source instanceof LargeInteger) {
            return ((LargeInteger) source).bigDecimalValue();
        } else if (source instanceof BigInteger) {
            return new BigDecimal((BigInteger) source);
        } else if (source instanceof BigDecimal) {
            return (BigDecimal) source;
        } else if (source instanceof Long) {
            return BigDecimal.valueOf((Long) source);
        } else if (source instanceof Number) {
            return BigDecimal.valueOf(((Number) source).doubleValue());
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? BigDecimal.ONE: BigDecimal.ZERO;
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            int scale = reader.readInt();
            BigInteger bigIntegerValue = new BigInteger(reader.readRemaining());
            return new BigDecimal(bigIntegerValue, scale);
        } else if (source instanceof BlobValue) {
            ByteString.Reader reader = ((BlobValue) source).contentAccess().get().reader();
            int scale = reader.readInt();
            BigInteger bigIntegerValue = new BigInteger(reader.readRemaining());
            return new BigDecimal(bigIntegerValue, scale);
        } else if (source instanceof Character) {
            return BigDecimal.valueOf((long) (char) source); // NOSONAR it's better to be explicit
        } else if (source instanceof LocalTime) {
            return BigDecimal.valueOf(((LocalTime) source).toNanoOfDay() / 1_000_000_000d);
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDate) {
            return BigDecimal.valueOf(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalDateTime) {
            long secondsSinceEpoch = ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
            double fragmentOfSecond = ((LocalDateTime) source).getNano() / 1_000_000_000d;
            return BigDecimal.valueOf(secondsSinceEpoch + fragmentOfSecond);
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof ZonedDateTime) {
            return convert(((ZonedDateTime) source).toInstant());
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            long secondsSinceEpoch = ((Instant) source).getEpochSecond();
            double fragmentOfSecond = ((Instant) source).getNano() / 1_000_000_000d;
            return BigDecimal.valueOf(secondsSinceEpoch + fragmentOfSecond);
        } else if (source instanceof ZoneOffset) {
            return BigDecimal.valueOf(((ZoneOffset) source).getTotalSeconds());
        } else if (source instanceof DateTimeDelta) {
            Duration duration = ((DateTimeDelta) source).toCollapsedDuration();
            BigDecimal bigDecimalSeconds = BigDecimal.valueOf(duration.getSeconds());
            return bigDecimalSeconds.add(new BigDecimal(BigInteger.valueOf(duration.getNano()), 9));
        } else if (source instanceof Duration) {
            Duration duration = (Duration) source;
            BigDecimal bigDecimalSeconds = BigDecimal.valueOf(duration.getSeconds());
            return bigDecimalSeconds.add(new BigDecimal(BigInteger.valueOf(duration.getNano()), 9));
        } else if (source instanceof Period) {
            Period period = (Period) source;
            return BigDecimal.valueOf(period.getYears() * 365)
                    .add(BigDecimal.valueOf(period.getMonths() * 30))
                    .add(BigDecimal.valueOf(period.getDays()));
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString());
        }
    }

}
