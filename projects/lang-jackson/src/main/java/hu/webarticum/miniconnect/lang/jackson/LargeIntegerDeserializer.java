package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import hu.webarticum.miniconnect.lang.LargeInteger;

public class LargeIntegerDeserializer extends StdDeserializer<LargeInteger> {

    private static final long serialVersionUID = 1L;
    

    protected LargeIntegerDeserializer() {
        super(LargeInteger.class);
    }

    
    @Override
    public LargeInteger deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return LargeInteger.of(parser.readValueAs(String.class));
    }
    
}