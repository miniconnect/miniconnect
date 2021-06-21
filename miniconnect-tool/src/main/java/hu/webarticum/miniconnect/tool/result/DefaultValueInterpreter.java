package hu.webarticum.miniconnect.tool.result;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.util.data.ByteString;

// FIXME: this is a very dummy implementation
public class DefaultValueInterpreter implements ValueInterpreter {

    public static final StoredValueDefinition DEFAULT_DEFINITION =
            new StoredValueDefinition(ByteString.class.getName());
    

    private final Class<?> type;
    
    // FIXME: use?
    private final MiniValueDefinition definition;


    public DefaultValueInterpreter(MiniValueDefinition definition) {
        try {
            this.type = Class.forName(definition.type());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        
        this.definition = definition;
    }
    
    public DefaultValueInterpreter(Class<?> type) {
        this.type = type;
        this.definition = new StoredValueDefinition(type.getName());
    }


    public Class<?> type() {
        return type;
    }

    public MiniValueDefinition definition() {
        return definition;
    }
    
    @Override
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
        } else if (value instanceof ByteString) {
            ByteString byteStringValue = (ByteString) value;
            return new StoredValue(byteStringValue);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", value.getClass().getSimpleName()));
        }
    }

    @Override
    public Object decode(MiniValue value) {
        if (value.isNull()) {
            return null;
        }

        ByteString content = value.contentAccess().get();
        if (type.equals(Boolean.class)) {
            return (content.byteAt(0) != ((byte) 0));
        } else if (type.equals(Byte.class)) {
            return content.byteAt(0);
        } else if (type.equals(Short.class)) {
            ByteBuffer byteBuffer = content.asBuffer();
            return byteBuffer.getShort();
        } else if (type.equals(Integer.class)) {
            ByteBuffer byteBuffer = content.asBuffer();
            return byteBuffer.getInt();
        } else if (type.equals(Long.class)) {
            ByteBuffer byteBuffer = content.asBuffer();
            return byteBuffer.getLong();
        } else if (type.equals(String.class)) {
            return content.toString(StandardCharsets.UTF_8);
        } else {
            throw new IllegalStateException(
                    String.format("Unsupported type: %s", type.getSimpleName()));
        }
    }

}
