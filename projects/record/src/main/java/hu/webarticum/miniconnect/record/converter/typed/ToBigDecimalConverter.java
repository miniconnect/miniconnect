package hu.webarticum.miniconnect.record.converter.typed;

import java.math.BigDecimal;
import java.math.BigInteger;

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
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString());
        }
    }

}
