package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToBigDecimalConverter implements TypedConverter<BigDecimal> {
    
    @Override
    public Class<BigDecimal> targetClazz() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal convert(Object source) {
        if (source instanceof BigDecimal) {
            return (BigDecimal) source;
        } else if (source instanceof BigInteger) {
            return new BigDecimal((BigInteger) source);
        } else if (source instanceof Long) {
            return BigDecimal.valueOf((Long) source);
        } else if (source instanceof Number) {
            return BigDecimal.valueOf(((Number) source).doubleValue());
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? BigDecimal.ONE: BigDecimal.ZERO;
        } else if (source instanceof Character) {
            return BigDecimal.valueOf((long) (char) source); // NOSONAR it's better to be explicit
        } else if (source instanceof LocalDate) {
            return BigDecimal.valueOf(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalTime) {
            return BigDecimal.valueOf(((LocalTime) source).toNanoOfDay() / 1_000_000_000d);
        } else if (source instanceof LocalDateTime) {
            long secondsSinceEpoch = ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
            double fragmentOfSecond = ((LocalDateTime) source).getNano() / 1_000_000_000d;
            return BigDecimal.valueOf(secondsSinceEpoch + fragmentOfSecond);
        } else if (source instanceof Instant) {
            long secondsSinceEpoch = ((Instant) source).getEpochSecond();
            double fragmentOfSecond = ((Instant) source).getNano() / 1_000_000_000d;
            return BigDecimal.valueOf(secondsSinceEpoch + fragmentOfSecond);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString());
        }
    }

}
