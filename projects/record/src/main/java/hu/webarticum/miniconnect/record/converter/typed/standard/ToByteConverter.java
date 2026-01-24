package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class ToByteConverter implements TypedConverter<Byte> {

    @Override
    public Class<Byte> targetClazz() {
        return Byte.class;
    }

    @Override
    public Byte convert(Object source) {
        if (source instanceof Byte) {
            return (Byte) source;
        } else if (source instanceof Number) {
            return ((Number) source).byteValue();
        } else if (source instanceof Boolean) {
            return (boolean) source ? (byte) 1 : (byte) 0;
        } else if (source instanceof ByteString) {
            ByteString byteStringValue = (ByteString) source;
            return byteStringValue.isEmpty() ? (byte) 0 : byteStringValue.byteAt(0);
        } else if (source instanceof BlobValue) {
            ByteString byteStringValue = ((BlobValue) source).contentAccess().get();
            return byteStringValue.isEmpty() ? (byte) 0 : byteStringValue.byteAt(0);
        } else {
            return new BigDecimal(source.toString()).toBigInteger().byteValueExact();
        }
    }

}
