package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class ImmutableListSerializer extends StdSerializer<ImmutableList<?>> {
    
    private static final long serialVersionUID = 1L;
    

    @SuppressWarnings("unchecked")
    public ImmutableListSerializer() {
        super((Class<ImmutableList<?>>) (Class<?>) ImmutableList.class);
    }

    @Override
    public void serialize(ImmutableList<?> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartArray(value, value.size());
        for (Object item : value) {
            generator.writeObject(item);
        }
        generator.writeEndArray();
    }
    
}