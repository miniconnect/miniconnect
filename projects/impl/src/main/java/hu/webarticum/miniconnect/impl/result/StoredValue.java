package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;

public final class StoredValue implements MiniValue, Serializable {

    private static final long serialVersionUID = 1L;

    
    public static final StoredValueDefinition DEFAULT_DEFINITION =
            new StoredValueDefinition(ByteString.class.getName());
    


    private final StoredValueDefinition definition;
    
    private final boolean isNull;

    private final StoredContentAccess contentAccess;


    public StoredValue() {
        this(true, ByteString.empty());
    }

    public StoredValue(ByteString content) {
        this(false, content);
    }

    public StoredValue(boolean isNull, ByteString content) {
        this(DEFAULT_DEFINITION, isNull, content);
    }
    
    public StoredValue(MiniValueDefinition definition, boolean isNull, ByteString content) {
        this.definition = StoredValueDefinition.of(definition);
        this.isNull = isNull;
        this.contentAccess = new StoredContentAccess(content);
    }

    public static StoredValue of(MiniValue value) {
        if (value instanceof StoredValue) {
            return (StoredValue) value;
        }

        MiniContentAccess contentAccess = value.contentAccess();
        if (contentAccess.isLarge()) {
            throw new IllegalArgumentException(
                    "Content is too large to store in memory");
        }
        
        return new StoredValue(
                StoredValueDefinition.of(value.definition()),
                value.isNull(),
                contentAccess.get());
    }


    @Override
    public MiniValueDefinition definition() {
        return definition;
    }

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

}
