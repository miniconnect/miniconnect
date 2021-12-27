package hu.webarticum.miniconnect.jdbc.converter;

public class ByteConverter implements SpecificConverter<Byte> {

    @Override
    public Byte convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? (byte) 1 : (byte) 0;
        } else {
            return Byte.parseByte(value.toString());
        }
    }

}
