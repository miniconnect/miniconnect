package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;


public class ToBitStringConverter implements TypedConverter<BitString> {

    @Override
    public Class<BitString> targetClazz() {
        return BitString.class;
    }

    @Override
    public BitString convert(Object source) {
        if (source instanceof BitString) {
            return (BitString) source;
        } else if (source instanceof ByteString) {
            return BitString.of(((ByteString) source).extract());
        } else if (source instanceof String) {
            return BitString.of((String) source);
        } else if (source instanceof BlobValue) {
            MiniContentAccess contentAccess = ((BlobValue) source).contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large BLOB");
            }
            return BitString.of(contentAccess.get().extract());
        } else if (source instanceof ClobValue) {
            MiniContentAccess contentAccess = ((ClobValue) source).contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large CLOB");
            }
            return BitString.of(contentAccess.get().extract());
        } else if (source instanceof Boolean) {
            return BitString.of((boolean) source);
        } else if (source instanceof Byte) {
            return BitString.of(new byte[] { (byte) source });
        } else if (source instanceof Short) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.asShortBuffer().put((short) source);
            return BitString.of(byteBuffer.array());
        } else if (source instanceof Integer) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            byteBuffer.asIntBuffer().put((int) source);
            return BitString.of(byteBuffer.array());
        } else if (source instanceof Long) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBuffer.asLongBuffer().put((long) source);
            return BitString.of(byteBuffer.array());
        } else if (source instanceof LargeInteger) {
            LargeInteger largeInteger = (LargeInteger) source;
            int bitLength = largeInteger.bitLength();
            if (largeInteger.isNegative()) {
                bitLength++;
            }
            BitString converted = BitString.of(largeInteger.toByteArray());
            int convertedSize = converted.length();
            return converted.substring(convertedSize - bitLength, convertedSize);
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return convert(new ToLargeIntegerConverter().convert(source));
        }
    }

}
