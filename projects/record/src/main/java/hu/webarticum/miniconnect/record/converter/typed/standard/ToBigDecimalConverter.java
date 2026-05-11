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

import hu.webarticum.miniconnect.lang.BitString;
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
        } else if (
            source instanceof Long ||
            source instanceof Integer ||
            source instanceof Short ||
            source instanceof Byte
        ) {
            return BigDecimal.valueOf(((Number) source).longValue());
        } else if (source instanceof Number) {
            return BigDecimal.valueOf(((Number) source).doubleValue());
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? BigDecimal.ONE: BigDecimal.ZERO;
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
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
            return localTimeToBigDecimal((LocalTime) source);
        } else if (source instanceof OffsetTime) {
            return localTimeToBigDecimal(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDate) {
            return BigDecimal.valueOf(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalDateTime) {
            return instantToBigDecimal(((LocalDateTime) source).toInstant(ZoneOffset.UTC));
        } else if (source instanceof OffsetDateTime) {
            return instantToBigDecimal(((OffsetDateTime) source).toInstant());
        } else if (source instanceof ZonedDateTime) {
            return instantToBigDecimal(((ZonedDateTime) source).toInstant());
        } else if (source instanceof Timestamp) {
            return instantToBigDecimal(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            return instantToBigDecimal((Instant) source);
        } else if (source instanceof ZoneOffset) {
            return BigDecimal.valueOf(((ZoneOffset) source).getTotalSeconds());
        } else if (source instanceof DateTimeDelta) {
            return durationToBigDecimal(((DateTimeDelta) source).toCollapsedDuration());
        } else if (source instanceof Duration) {
            return durationToBigDecimal((Duration) source);
        } else if (source instanceof Period) {
            return periodToBigDecimal((Period) source);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString());
        }
    }

    private BigDecimal instantToBigDecimal(Instant instant) {
        return temporalToBigDecimal(instant.getEpochSecond(), instant.getNano());
    }

    private BigDecimal localTimeToBigDecimal(LocalTime time) {
        return temporalToBigDecimal(time.toSecondOfDay(), time.getNano());
    }

    private BigDecimal durationToBigDecimal(Duration duration) {
        return temporalToBigDecimal(duration.getSeconds(), duration.getNano());
    }

    private BigDecimal temporalToBigDecimal(long seconds, int nanos) {
        BigDecimal result = BigDecimal.valueOf(seconds);
        if (nanos != 0) {
            result = result.add(new BigDecimal(BigInteger.valueOf(nanos), 9).stripTrailingZeros());
        }
        return result;
    }

    private BigDecimal periodToBigDecimal(Period period) {
        BigDecimal result = BigDecimal.valueOf(period.getYears() * 365L);
        result = result.add(BigDecimal.valueOf(period.getMonths() * 30L));
        result = result.add(BigDecimal.valueOf(period.getDays()));
        return result;
    }

}
