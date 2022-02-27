package hu.webarticum.miniconnect.record.converter.typed.extra;

import java.util.Date;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToInstantConverter;

public class ToDateConverter implements TypedConverter<Date> {
    
    @Override
    public Class<Date> targetClazz() {
        return Date.class;
    }

    @Override
    public Date convert(Object source) {
        return Date.from(new ToInstantConverter().convert(source));
    }

}
