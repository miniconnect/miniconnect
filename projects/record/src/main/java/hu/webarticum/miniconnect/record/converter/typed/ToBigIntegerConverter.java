package hu.webarticum.miniconnect.record.converter.typed;

import java.math.BigDecimal;
import java.math.BigInteger;

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
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigInteger(source.toString());
        }
    }

}
