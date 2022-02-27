package hu.webarticum.miniconnect.record.converter.typed.extra;

import java.io.InputStream;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToBlobValueConverter;

public class ToInputStreamConverter implements TypedConverter<InputStream> {
    
    @Override
    public Class<InputStream> targetClazz() {
        return InputStream.class;
    }

    @Override
    public InputStream convert(Object source) {
        return new ToBlobValueConverter().convert(source).inputStream();
    }

}
