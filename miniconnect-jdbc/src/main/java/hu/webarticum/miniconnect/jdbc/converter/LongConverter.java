package hu.webarticum.miniconnect.jdbc.converter;

public class LongConverter implements SpecificConverter<Long> {

    @Override
    public Long convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? 1L : 0L;
        } else {
            return Long.parseLong(value.toString());
        }
    }

}
