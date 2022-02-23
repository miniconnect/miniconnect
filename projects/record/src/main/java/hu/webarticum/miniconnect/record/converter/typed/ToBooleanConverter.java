package hu.webarticum.miniconnect.record.converter.typed;

import java.util.regex.Pattern;

import hu.webarticum.miniconnect.record.custom.CustomValue;

public class ToBooleanConverter implements TypedConverter<Boolean> {
    
    public static final Pattern FALSE_PATTERN = Pattern.compile(
            "f|false|off|n|no|disabled|0|0?\\.0+|\\s*",
            Pattern.CASE_INSENSITIVE);
    

    @Override
    public Class<Boolean> targetClazz() {
        return Boolean.class;
    }

    @Override
    public Boolean convert(Object source) {
        if (source instanceof Boolean) {
            return (Boolean) source;
        } else if (source instanceof Number) {
            return ((Number) source).doubleValue() == 0d;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return !FALSE_PATTERN.matcher(source.toString()).matches();
        }
    }

}
