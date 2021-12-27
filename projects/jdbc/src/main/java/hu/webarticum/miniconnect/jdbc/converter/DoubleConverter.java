package hu.webarticum.miniconnect.jdbc.converter;

public class DoubleConverter implements SpecificConverter<Double> {

    @Override
    public Double convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? 1.0 : 0.0;
        } else {
            return Double.parseDouble(value.toString());
        }
    }

}
