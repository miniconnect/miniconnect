package hu.webarticum.miniconnect.record.converter.typed;

public class ToByteConverter implements TypedConverter<Byte> {
    
    @Override
    public Class<Byte> targetClazz() {
        return Byte.class;
    }

    @Override
    public Byte convert(Object source) {
        if (source instanceof Number) {
            return ((Number) source).byteValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? (byte) 1 : (byte) 0;
        } else if (source instanceof Character) {
            return ((byte) (char) source);
        } else {
            return Byte.valueOf(source.toString());
        }
    }

}
