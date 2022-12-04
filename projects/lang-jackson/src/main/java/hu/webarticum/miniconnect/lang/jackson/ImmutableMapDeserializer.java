package hu.webarticum.miniconnect.lang.jackson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

import hu.webarticum.miniconnect.lang.ImmutableMap;

public class ImmutableMapDeserializer extends StdDeserializer<ImmutableMap<?, ?>> implements ContextualDeserializer {

    private static final long serialVersionUID = 1L;
    
    
    protected ImmutableMapDeserializer() {
        super(ImmutableMap.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ImmutableMap<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return ImmutableMap.fromMap(parser.readValueAs((Class<Map<?, ?>>) (Class<?>) Map.class));
    }

    @Override
    public JsonDeserializer<?> createContextual(
            DeserializationContext context, BeanProperty property) throws JsonMappingException {
        JavaType mapType = property != null ? property.getType() : context.getContextualType();
        JavaType keyType = mapType.containedType(0);
        JavaType valueType = mapType.containedType(1);
        return new ImmutableListContextualDeserializer(keyType, valueType);
    }
    
    
    private static class ImmutableListContextualDeserializer extends StdDeserializer<ImmutableMap<?, ?>> {
        
        private static final long serialVersionUID = 1L;

        
        private final JavaType keyType;
        
        private final JavaType valueType;

        
        protected ImmutableListContextualDeserializer(JavaType keyType, JavaType valueType) {
            super(ImmutableMap.class);
            this.keyType = keyType;
            this.valueType = valueType;
        }


        @Override
        public ImmutableMap<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            MapType mapType = context.getTypeFactory().constructMapType(HashMap.class, keyType, valueType);
            TypeReference<?> mapTypeReference = new TypeReference<Object>() {
                
                @Override
                public Type getType() {
                    return mapType;
                }
                
            };
            return ImmutableMap.fromMap(parser.readValueAs(mapTypeReference));
        }

    }
    
}