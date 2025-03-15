package hu.webarticum.miniconnect.record.custom.schema;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class StandardSchema implements Schema {
    
    public static final byte FLAG = (byte) 1;
    
    
    private final StandardValueType valueType;
    
    private final ImmutableMap<String, ByteString> properties;
    
    private final ValueTranslator translator;
    

    public StandardSchema(StandardValueType valueType) {
        this(valueType, ImmutableMap.empty());
    }
    
    public StandardSchema(
            StandardValueType valueType,
            ImmutableMap<String, ByteString> properties) {
        this.valueType = valueType;
        this.properties = properties;
        this.translator = valueType.translatorFor(properties);
    }
    

    public static StandardSchema readMainFrom(InputStream in) {
        ByteString standardFlag = StreamUtil.readFixedBytes(in, StandardValueType.FLAG_LENGTH);
        StandardValueType valueType = StandardValueType.ofFlag(standardFlag);
        int propertiesSize = StreamUtil.readInt(in);
        Map<String, ByteString> propertiesBuilder = new HashMap<>();
        for (int i = 0; i < propertiesSize; i++) {
            String key = StreamUtil.readString(in);
            ByteString value = StreamUtil.readBytes(in);
            propertiesBuilder.put(key, value);
        }
        ImmutableMap<String, ByteString> properties = ImmutableMap.fromMap(propertiesBuilder);
        return new StandardSchema(valueType, properties);
    }

    
    @Override
    public void writeTo(OutputStream out) {
        StreamUtil.write(out, FLAG);
        StreamUtil.writeFixedBytes(out, valueType.flag());
        StreamUtil.writeInt(out, properties.size());
        for (Map.Entry<String, ByteString> entry : properties.entrySet()) {
            StreamUtil.writeString(out, entry.getKey());
            StreamUtil.writeBytes(out, entry.getValue());
        }
    }

    @Override
    public Object readValueFrom(InputStream in) {
        int length = translator.length();
        if (length == MiniValueDefinition.DYNAMIC_LENGTH) {
            length = StreamUtil.readInt(in);
        }
        ByteString valueBytes = StreamUtil.readFixedBytes(in, length);
        return translator.decode(new StoredContentAccess(valueBytes));
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        ByteString valueBytes;
        try (MiniContentAccess contentAccess = translator.encode(value)) {
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Value represention is too large");
            }
            valueBytes = contentAccess.get();
        }
        int expectedLength = translator.length();
        int actualLength = valueBytes.length();
        if (expectedLength == MiniValueDefinition.DYNAMIC_LENGTH) {
            StreamUtil.writeInt(out, actualLength);
        } else if (actualLength != expectedLength) {
            throw new IllegalArgumentException(String.format(
                    "Unexpected length of representation: %d, expected: %d",
                    actualLength,
                    expectedLength));
        }
        StreamUtil.writeFixedBytes(out, valueBytes);
    }

}
