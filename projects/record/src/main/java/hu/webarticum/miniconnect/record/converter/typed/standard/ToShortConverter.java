package hu.webarticum.miniconnect.record.converter.typed.standard;

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class ToShortConverter implements TypedConverter<Short> {

    @Override
    public Class<Short> targetClazz() {
        return Short.class;
    }

    @Override
    public Short convert(Object source) {
        if (source instanceof Short) {
            return ((Short) source);
        } else if (source instanceof Number) {
            return ((Number) source).shortValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? (short) 1 : (short) 0;
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            return ((ByteString) source).reader().readShort();
        } else if (source instanceof BlobValue) {
            return ((BlobValue) source).contentAccess().get().reader().readShort();
        } else {
            return new ToLongConverter().convert(source).shortValue();
        }
    }

}
