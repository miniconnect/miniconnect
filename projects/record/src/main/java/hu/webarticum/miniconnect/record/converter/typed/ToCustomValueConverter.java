package hu.webarticum.miniconnect.record.converter.typed;

import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToCustomValueConverter implements TypedConverter<CustomValue> {
    
    @Override
    public Class<CustomValue> targetClazz() {
        return CustomValue.class;
    }

    @Override
    public CustomValue convert(Object source) {
        if (source instanceof CustomValue) {
            return (CustomValue) source;
        } else {
            return new CustomValue(source);
        }
    }

}
