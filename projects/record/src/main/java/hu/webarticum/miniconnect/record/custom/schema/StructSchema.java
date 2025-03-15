package hu.webarticum.miniconnect.record.custom.schema;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class StructSchema implements Schema {
    
    public static final byte FLAG = (byte) 'S';
    
    
    private final ImmutableMap<String, Schema> schemas;
    
    private final ImmutableList<String> keyOrder;
    
    
    public StructSchema(ImmutableMap<String, Schema> schemas, ImmutableList<String> keyOrder) {
        this.schemas = schemas;
        this.keyOrder = keyOrder;
    }
    

    public static StructSchema readMainFrom(InputStream in) {
        int structSize = StreamUtil.readInt(in);
        Map<String, Schema> schemasBuilder = new HashMap<>(structSize);
        List<String> keyOrderBuilder = new ArrayList<>(structSize);
        for (int i = 0; i < structSize; i++) {
            String key = StreamUtil.readString(in);
            Schema schema = Schema.readFrom(in);
            schemasBuilder.put(key, schema);
            keyOrderBuilder.add(key);
        }
        return new StructSchema(
                ImmutableMap.fromMap(schemasBuilder),
                ImmutableList.fromCollection(keyOrderBuilder));
    }

    
    @Override
    public void writeTo(OutputStream out) {
        int structSize = keyOrder.size();
        StreamUtil.writeInt(out, structSize);
        for (String key : keyOrder) {
            StreamUtil.writeString(out, key);
            Schema schema = schemas.get(key);
            schema.writeTo(out);
        }
    }

    @Override
    public Object readValueFrom(InputStream in) {
        int structSize = keyOrder.size();
        Map<String, Object> resultBuilder = new HashMap<>(structSize);
        for (String key : keyOrder) {
            Schema schema = schemas.get(key);
            Object value = schema.readValueFrom(in);
            resultBuilder.put(key, value);
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        @SuppressWarnings("unchecked")
        ImmutableMap<String, Object> structValue = (ImmutableMap<String, Object>) value;
        for (String key : keyOrder) {
            Schema schema = schemas.get(key);
            Object item = structValue.get(key);
            schema.writeValueTo(item, out);
        }
    }

}
