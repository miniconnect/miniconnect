package hu.webarticum.miniconnect.record.converter.typed.standard;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToIntegerConverter implements TypedConverter<Integer> {
    
    @Override
    public Class<Integer> targetClazz() {
        return Integer.class;
    }

    @Override
    public Integer convert(Object source) {
        if (source instanceof Integer) {
            return ((Integer) source);
        } else if (source instanceof Number) {
            return ((Number) source).intValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1 : 0;
        } else if (source instanceof Character) {
            return ((int) (char) source);
        } else {
            return new ToLongConverter().convert(source).intValue();
        }
    }

}
