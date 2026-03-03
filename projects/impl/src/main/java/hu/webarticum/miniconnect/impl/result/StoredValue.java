package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;

public final class StoredValue implements MiniValue, Serializable {

    private static final long serialVersionUID = 1L;


    public static final StoredValueDefinition DEFAULT_DEFINITION = StoredValueDefinition.of(ByteString.class.getName());


    private final StoredValueDefinition definition;

    private final boolean isNull;

    private final StoredContentAccess contentAccess;


    private StoredValue(StoredValueDefinition definition, boolean isNull, StoredContentAccess contentAccess) {
        this.definition = definition;
        this.isNull = isNull;
        this.contentAccess = contentAccess;
    }

    public static StoredValue of(StoredValueDefinition definition, boolean isNull, StoredContentAccess contentAccess) {
        return new StoredValue(definition, isNull, contentAccess);
    }

    public static StoredValue of(StoredValueDefinition definition, boolean isNull, ByteString content) {
        return of(definition, isNull, StoredContentAccess.of(content));
    }

    public static StoredValue of(boolean isNull, ByteString content) {
        return of(DEFAULT_DEFINITION, isNull, content);
    }

    public static StoredValue of(ByteString content) {
        return of(false, content);
    }

    public static StoredValue empty() {
        return of(true, ByteString.empty());
    }

    public static StoredValue from(MiniValueDefinition definition, boolean isNull, MiniContentAccess contentAccess) {
        return new StoredValue( StoredValueDefinition.from(definition), isNull, StoredContentAccess.from(contentAccess));
    }

    public static StoredValue from(MiniValue value) {
        if (value instanceof StoredValue) {
            return (StoredValue) value;
        }

        return from(value.definition(), value.isNull(), value.contentAccess());
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
    public MiniContentAccess contentAccess(boolean keep) {
        return contentAccess;
    }

}
