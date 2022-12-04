package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class ImmutableListDeserializer extends StdDeserializer<ImmutableList<?>> implements ContextualDeserializer {

    private static final long serialVersionUID = 1L;
    
    
    protected ImmutableListDeserializer() {
        super(ImmutableList.class);
    }
    
    
    @Override
    public ImmutableList<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return ImmutableList.of(parser.readValueAs(Object[].class));
    }

    @Override
    public JsonDeserializer<?> createContextual(
            DeserializationContext context, BeanProperty property) throws JsonMappingException {
        JavaType listType = property != null ? property.getType() : context.getContextualType();
        JavaType itemType = listType.containedType(0);
        return new ImmutableListContextualDeserializer(itemType);
    }
    
    
    private static class ImmutableListContextualDeserializer extends StdDeserializer<ImmutableList<?>> {
        
        private static final long serialVersionUID = 1L;
        
        
        private final JavaType itemType;

        
        protected ImmutableListContextualDeserializer(JavaType itemType) {
            super(ImmutableList.class);
            this.itemType = itemType;
        }


        @Override
        public ImmutableList<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            ArrayType arrayType = context.getTypeFactory().constructArrayType(itemType);
            TypeReference<?> arrayTypeReference = new TypeReference<Object>() {
                
                @Override
                public Type getType() {
                    return arrayType;
                }
                
            };
            return ImmutableList.of(parser.readValueAs(arrayTypeReference));
        }

    }
    
}