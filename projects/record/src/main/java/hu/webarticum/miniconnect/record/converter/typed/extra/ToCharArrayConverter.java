package hu.webarticum.miniconnect.record.converter.typed.extra;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToStringConverter;

public class ToCharArrayConverter implements TypedConverter<char[]> {
    
    @Override
    public Class<char[]> targetClazz() {
        return char[].class;
    }

    @Override
    public char[] convert(Object source) {
        return new ToStringConverter().convert(source).toCharArray();
    }

}
