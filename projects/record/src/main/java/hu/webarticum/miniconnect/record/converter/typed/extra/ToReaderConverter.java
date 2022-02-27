package hu.webarticum.miniconnect.record.converter.typed.extra;

import java.io.Reader;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToClobValueConverter;

public class ToReaderConverter implements TypedConverter<Reader> {
    
    @Override
    public Class<Reader> targetClazz() {
        return Reader.class;
    }

    @Override
    public Reader convert(Object source) {
        return new ToClobValueConverter().convert(source).reader();
    }

}
