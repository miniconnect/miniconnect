package hu.webarticum.miniconnect.record.converter.typed;

public class ToShortConverter implements TypedConverter<Short> {
    
    @Override
    public Class<Short> targetClazz() {
        return Short.class;
    }

    @Override
    public Short convert(Object source) {
        if (source instanceof Number) {
            return ((Number) source).shortValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? (short) 1 : (short) 0;
        } else if (source instanceof Character) {
            return ((short) (char) source);
        } else {
            return Short.valueOf(source.toString());
        }
    }

}
