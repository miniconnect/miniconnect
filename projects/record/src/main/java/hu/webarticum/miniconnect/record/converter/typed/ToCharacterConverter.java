package hu.webarticum.miniconnect.record.converter.typed;

public class ToCharacterConverter implements TypedConverter<Character> {
    
    @Override
    public Class<Character> targetClazz() {
        return Character.class;
    }

    @Override
    public Character convert(Object source) {
        if (source instanceof Number) {
            return (char) ((Number) source).shortValue();
        } else if (source instanceof Character) {
            return ((Character) source);
        } else {
            String stringValue = source.toString();
            return stringValue.isEmpty() ? '\0' : stringValue.charAt(0);
        }
    }

}
