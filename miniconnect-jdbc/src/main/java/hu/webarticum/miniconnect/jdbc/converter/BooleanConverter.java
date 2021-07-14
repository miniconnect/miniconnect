package hu.webarticum.miniconnect.jdbc.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BooleanConverter implements SpecificConverter<Boolean> {
    
    private static final Set<String> FALSY_LITERALS = new HashSet<>(Arrays.asList(
            "", "0", "false", "n", "no", "off", "disabled"));
    

    @Override
    public Boolean convert(Object value, Object modifier) {
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return (((Number) value).doubleValue() != 0.0);
        } else {
            String stringValue = value.toString().toLowerCase();
            return !FALSY_LITERALS.contains(stringValue);
        }
    }

}
