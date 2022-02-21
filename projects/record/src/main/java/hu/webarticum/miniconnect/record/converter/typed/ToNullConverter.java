package hu.webarticum.miniconnect.record.converter.typed;

public class ToNullConverter implements TypedConverter<Void> {

    @Override
    public Class<Void> targetClazz() {
        return Void.class;
    }

    @Override
    public Void convert(Object source) {
        return null;
    }

}
