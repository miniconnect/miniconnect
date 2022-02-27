package hu.webarticum.miniconnect.record.converter.typed.extra;

import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToByteStringConverter;

public class ToByteArrayConverter implements TypedConverter<byte[]> {
    
    @Override
    public Class<byte[]> targetClazz() {
        return byte[].class;
    }

    @Override
    public byte[] convert(Object source) {
        return new ToByteStringConverter().convert(source).extract();
    }

}
