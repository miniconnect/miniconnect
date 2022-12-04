package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import hu.webarticum.miniconnect.lang.ImmutableMap;

public class ImmutableMapSerializer extends StdSerializer<ImmutableMap<?, ?>> {
    
    private static final long serialVersionUID = 1L;
    

    @SuppressWarnings("unchecked")
    public ImmutableMapSerializer() {
        super((Class<ImmutableMap<?, ?>>) (Class<?>) ImmutableMap.class);
    }

    @Override
    public void serialize(
            ImmutableMap<?, ?> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject(value, value.size());
        for (Map.Entry<?, ?> item : value.entrySet()) {
            generator.writeObjectField(item.getKey().toString(), item.getValue()); // FIXME
        }
        generator.writeEndObject();
    }
    
}