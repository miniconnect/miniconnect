package hu.webarticum.miniconnect.jdbc.converter;

public class FloatConverter implements SpecificConverter<Float> {

    @Override
    public Float convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? 1f : 0f;
        } else {
            return Float.parseFloat(value.toString());
        }
    }

}
