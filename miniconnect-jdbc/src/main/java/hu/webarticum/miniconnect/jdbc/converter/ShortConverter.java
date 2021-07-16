package hu.webarticum.miniconnect.jdbc.converter;

public class ShortConverter implements SpecificConverter<Short> {

    @Override
    public Short convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? (short) 1 : (short) 0;
        } else {
            return Short.parseShort(value.toString());
        }
    }

}
