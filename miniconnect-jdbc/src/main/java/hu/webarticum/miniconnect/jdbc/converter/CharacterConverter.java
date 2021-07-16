package hu.webarticum.miniconnect.jdbc.converter;

public class CharacterConverter implements SpecificConverter<Character> {

    @Override
    public Character convert(Object value, Object modifier) {
        if (value instanceof Number) {
            return (char) ((Number) value).intValue();
        }
        
        String stringValue = value.toString();
        if (stringValue.isEmpty()) {
            return null;
        }
        
        return stringValue.charAt(0);
    }

}
