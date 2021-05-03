package hu.webarticum.miniconnect.tool.result;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ByteString;

// FIXME: this is a very dummy implementation
public class XxxValueEncoder {

    private final Class<?> type;


    public XxxValueEncoder(MiniColumnHeader columnHeader) {
        try {
            this.type = Class.forName(columnHeader.type());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public XxxValueEncoder(Class<?> type) {
        this.type = type;
    }


    public MiniColumnHeader columnHeader(String columnName) {
        return new StoredColumnHeader(columnName, type.getName());
    }

    public MiniValue encode(Object value) {
        if (value == null) {
            return new StoredValue();
        }

        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(String.format(
                    "Invalid value: expected: '%s', but found: '%s'",
                    type.getName(),
                    value.getClass().getName()));
        }

        if (value instanceof Boolean) {
            boolean booleanValue = (boolean) value;
            return new StoredValue(ByteString.wrap(
                    new byte[] { (byte) (booleanValue ? 1 : 0) }));
        } else if (value instanceof Byte) {
            byte byteValue = (byte) value;
            return new StoredValue(ByteString.wrap(new byte[] { byteValue }));
        } else if (value instanceof Short) {
            short shortValue = (short) value;
            ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.putShort(shortValue);
            return new StoredValue(ByteString.wrap(byteBuffer.array()));
        } else if (value instanceof Integer) {
            int intValue = (int) value;
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            byteBuffer.putInt(intValue);
            return new StoredValue(ByteString.wrap(byteBuffer.array()));
        } else if (value instanceof Long) {
            long longValue = (long) value;
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBuffer.putLong(longValue);
            return new StoredValue(ByteString.wrap(byteBuffer.array()));
        } else if (value instanceof String) {
            String stringValue = (String) value;
            return new StoredValue(ByteString.wrap(
                    stringValue.getBytes(StandardCharsets.UTF_8)));
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", value.getClass().getSimpleName()));
        }
    }

    public Object decode(MiniValue value) {
        if (value.isNull()) {
            return null;
        } else if (type.equals(Boolean.class)) {
            return (value.shortContent().byteAt(0) != ((byte) 0));
        } else if (type.equals(Byte.class)) {
            return value.shortContent().byteAt(0);
        } else if (type.equals(Short.class)) {
            ByteBuffer byteBuffer = value.shortContent().asBuffer();
            return byteBuffer.getShort();
        } else if (type.equals(Integer.class)) {
            ByteBuffer byteBuffer = value.shortContent().asBuffer();
            return byteBuffer.getInt();
        } else if (type.equals(Long.class)) {
            ByteBuffer byteBuffer = value.shortContent().asBuffer();
            return byteBuffer.getLong();
        } else if (type.equals(String.class)) {
            return value.shortContent().toString(StandardCharsets.UTF_8);
        } else {
            throw new IllegalStateException(
                    String.format("Unsupported type: %s", type.getSimpleName()));
        }
    }

}
