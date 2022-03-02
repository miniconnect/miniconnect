package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToBigIntegerConverter implements TypedConverter<BigInteger> {
    
    @Override
    public Class<BigInteger> targetClazz() {
        return BigInteger.class;
    }

    @Override
    public BigInteger convert(Object source) {
        if (source instanceof BigInteger) {
            return (BigInteger) source;
        } else if (source instanceof BigDecimal) {
            return ((BigDecimal) source).toBigInteger();
        } else if (source instanceof Number) {
            return BigInteger.valueOf(((Number) source).longValue());
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? BigInteger.ONE: BigInteger.ZERO;
        } else if (source instanceof Character) {
            return BigInteger.valueOf((long) (char) source); // NOSONAR it's better to be explicit
        } else if (source instanceof LocalDate) {
            return BigInteger.valueOf(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalTime) {
            return BigInteger.valueOf(((LocalTime) source).getSecond());
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            return BigInteger.valueOf(((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC));
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof Instant) {
            return BigInteger.valueOf(((Instant) source).getEpochSecond());
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigInteger(source.toString());
        }
    }

}
