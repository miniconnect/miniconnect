package hu.webarticum.miniconnect.record.converter.typed;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ToStringConverter implements TypedConverter<String> {
    
    @Override
    public Class<String> targetClazz() {
        return String.class;
    }

    @Override
    public String convert(Object source) {
        if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else if (source instanceof ClobValue) {
            ClobValue clobValue = (ClobValue) source;
            MiniContentAccess contentAccess = clobValue.contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large CLOB");
            }
            return contentAccess.get().toString(clobValue.charset());
        } else if (source instanceof BlobValue) {
            MiniContentAccess contentAccess = ((BlobValue) source).contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large BLOB");
            }
            return contentAccess.get().toString();
        } else {
            return source.toString();
        }
    }

}
