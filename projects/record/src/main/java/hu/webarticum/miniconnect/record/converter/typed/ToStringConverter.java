package hu.webarticum.miniconnect.record.converter.typed;

import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToStringConverter implements TypedConverter<String> {
    
    @Override
    public Class<String> targetClazz() {
        return String.class;
    }

    @Override
    public String convert(Object source) {
        if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return source.toString();
        }
    }

}
