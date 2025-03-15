package hu.webarticum.miniconnect.record.converter.typed.standard;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ToBlobValueConverter implements TypedConverter<BlobValue> {
    
    @Override
    public Class<BlobValue> targetClazz() {
        return BlobValue.class;
    }

    @Override
    public BlobValue convert(Object source) {
        if (source instanceof BlobValue) {
            return (BlobValue) source;
        } else if (source instanceof ClobValue) {
            return ((ClobValue) source).toBlob();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            ByteString bytes = new ToByteStringConverter().convert(source);
            return BlobValue.of(new StoredContentAccess(bytes));
        }
    }

}
