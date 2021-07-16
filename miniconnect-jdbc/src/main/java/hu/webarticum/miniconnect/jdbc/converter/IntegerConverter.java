package hu.webarticum.miniconnect.jdbc.converter;

public class IntegerConverter implements SpecificConverter<Integer> {

    @Override
    public Integer convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? 1 : 0;
        } else {
            return Integer.parseInt(value.toString());
        }
    }

}
