package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.lang.ImmutableMap;

public class MapSchema implements Schema {
    
    public static final byte FLAG = (byte) 'M';
    
    
    private final Schema keySchema;
    
    private final Schema valueSchema;
    

    public MapSchema(Schema keySchema, Schema valueSchema) {
        this.keySchema = keySchema;
        this.valueSchema = valueSchema;
    }
    

    public static MapSchema readMainFrom(InputStream in) {
        Schema keySchema = Schema.readFrom(in);
        Schema valueSchema = Schema.readFrom(in);
        return new MapSchema(keySchema, valueSchema);
    }

    
    @Override
    public void writeTo(OutputStream out) {
        StreamUtil.write(out, FLAG);
        keySchema.writeTo(out);
        valueSchema.writeTo(out);
    }

    @Override
    public Object readValueFrom(InputStream in) {
        int mapSize = StreamUtil.readInt(in);
        Map<Object, Object> resultBuilder = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            Object key = keySchema.readValueFrom(in);
            Object value = valueSchema.readValueFrom(in);
            resultBuilder.put(key, value);
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        @SuppressWarnings("unchecked")
        ImmutableMap<Object, Object> mapValue = (ImmutableMap<Object, Object>) value;
        StreamUtil.writeInt(out, mapValue.size());
        for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
            keySchema.writeValueTo(entry.getKey(), out);
            valueSchema.writeValueTo(entry.getValue(), out);
        }
    }

}
