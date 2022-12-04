package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class LargeIntegerSerializer extends StdSerializer<LargeInteger> {
    
    private static final long serialVersionUID = 1L;
    

    public LargeIntegerSerializer() {
        super(LargeInteger.class);
    }

    @Override
    public void serialize(LargeInteger value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.toString());
    }
    
}