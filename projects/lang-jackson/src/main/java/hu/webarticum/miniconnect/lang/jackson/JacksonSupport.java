package hu.webarticum.miniconnect.lang.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;

public class JacksonSupport {

    private JacksonSupport() {
        // static class
    }
    
    
    public static ObjectMapper createMapper() {
        return new ObjectMapper().registerModule(createModule());
    }

    @SuppressWarnings("unchecked")
    public static Module createModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LargeInteger.class, new LargeIntegerSerializer());
        module.addDeserializer(LargeInteger.class, new LargeIntegerDeserializer());
        module.addSerializer(
                (Class<ImmutableList<?>>) (Class<?>) ImmutableList.class, new ImmutableListSerializer());
        module.addDeserializer(ImmutableList.class, new ImmutableListDeserializer());
        module.addSerializer(
                (Class<ImmutableMap<?, ?>>) (Class<?>) ImmutableMap.class, new ImmutableMapSerializer());
        module.addDeserializer(ImmutableMap.class, new ImmutableMapDeserializer());
        return module;
    }
    
}
