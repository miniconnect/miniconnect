package hu.webarticum.miniconnect.tool.result;

import java.math.BigDecimal;
import java.math.BigInteger;
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
            return new StoredValue(ByteString.ofShort((short) value));
        } else if (value instanceof Integer) {
            return new StoredValue(ByteString.ofInt((int) value));
        } else if (value instanceof Long) {
            return new StoredValue(ByteString.ofLong((long) value));
        } else if (value instanceof String) {
            String stringValue = (String) value;
            return new StoredValue(ByteString.wrap(
                    stringValue.getBytes(StandardCharsets.UTF_8)));
        } else if (value instanceof ByteString) {
            ByteString byteStringValue = (ByteString) value;
            return new StoredValue(byteStringValue);
        } else if (value instanceof BigInteger) {
            BigInteger bigIntegerValue = (BigInteger) value;
            return new StoredValue(ByteString.wrap(bigIntegerValue.toByteArray()));
        } else if (value instanceof BigDecimal) {
            ByteString.Builder builder = ByteString.builder();
            BigDecimal bigDecimalValue = (BigDecimal) value;
            builder.appendInt(bigDecimalValue.scale());
            BigInteger bigIntegerValue = bigDecimalValue.unscaledValue();
            builder.append(bigIntegerValue.toByteArray());
            return new StoredValue(builder.build());
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
            return content.reader().readShort();
        } else if (type.equals(Integer.class)) {
            return content.reader().readInt();
        } else if (type.equals(Long.class)) {
            return content.reader().readLong();
        } else if (type.equals(String.class)) {
            return content.toString(StandardCharsets.UTF_8);
        } else if (type.equals(ByteString.class)) {
            return content;
        } else if (type.equals(BigInteger.class)) {
            return new BigInteger(content.extract());
        } else if (type.equals(BigDecimal.class)) {
            ByteString.Reader reader = content.reader();
            int scale = reader.readInt();
            BigInteger bigIntegerValue = new BigInteger(reader.readRemaining());
            return new BigDecimal(bigIntegerValue, scale);
        } else {
            throw new IllegalStateException(
                    String.format("Unsupported type: %s", type.getSimpleName()));
        }
    }

}
