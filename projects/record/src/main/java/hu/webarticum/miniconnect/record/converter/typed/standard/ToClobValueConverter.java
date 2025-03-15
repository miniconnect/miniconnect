package hu.webarticum.miniconnect.record.converter.typed.standard;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ToClobValueConverter implements TypedConverter<ClobValue> {
    
    @Override
    public Class<ClobValue> targetClazz() {
        return ClobValue.class;
    }

    @Override
    public ClobValue convert(Object source) {
        if (source instanceof ClobValue) {
            return (ClobValue) source;
        } else if (source instanceof BlobValue) {
            return ((BlobValue) source).toClob();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            ByteString bytes = new ToByteStringConverter().convert(source);
            return ClobValue.of(new StoredContentAccess(bytes));
        }
    }

}
