package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public final class StoredValueDefinition implements MiniValueDefinition, Serializable {

    private static final long serialVersionUID = 1L;


    private final String type;

    private final int length;

    private final ImmutableMap<String, ByteString> properties;


    private StoredValueDefinition(String type, int length, ImmutableMap<String, ByteString> properties) {
        this.type = type;
        this.length = length;
        this.properties = properties;
    }

    public static StoredValueDefinition of(String type, int length, ImmutableMap<String, ByteString> properties) {
        return new StoredValueDefinition(type, length, properties);
    }

    public static StoredValueDefinition of(String type, int length) {
        return of(type, length, ImmutableMap.empty());
    }

    public static StoredValueDefinition of(String type) {
        return of(type, MiniValueDefinition.DYNAMIC_LENGTH);
    }

    public static StoredValueDefinition from(MiniValueDefinition valueDefinition) {
        if (valueDefinition instanceof StoredValueDefinition) {
            return (StoredValueDefinition) valueDefinition;
        }

        return of(valueDefinition.type(), valueDefinition.length(), valueDefinition.properties());
    }


    @Override
    public String type() {
        return type;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public ImmutableMap<String, ByteString> properties() {
        return properties;
    }

}
