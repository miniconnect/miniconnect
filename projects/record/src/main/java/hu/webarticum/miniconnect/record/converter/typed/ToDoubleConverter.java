package hu.webarticum.miniconnect.record.converter.typed;

import java.math.BigDecimal;

import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToDoubleConverter implements TypedConverter<Double> {
    
    @Override
    public Class<Double> targetClazz() {
        return Double.class;
    }

    @Override
    public Double convert(Object source) {
        if (source instanceof Double) {
            return ((Double) source);
        } else if (source instanceof Number) {
            return ((Number) source).doubleValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1d: 0d;
        } else if (source instanceof Character) {
            return ((double) (char) source);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).doubleValue();
        }
    }

}
