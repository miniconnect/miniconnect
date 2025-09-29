package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.sql.Timestamp;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;

public class ToTimestampConverter implements TypedConverter<Timestamp> {

    @Override
    public Class<Timestamp> targetClazz() {
        return Timestamp.class;
    }

    @Override
    public Timestamp convert(Object source) {
        if (source instanceof Timestamp) {
            return (Timestamp) source;
        } else {
            return Timestamp.from(new ToInstantConverter().convert(source));
        }
    }

}
