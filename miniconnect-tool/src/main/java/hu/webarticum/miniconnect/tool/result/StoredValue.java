package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.util.data.ByteString;

public class StoredValue implements MiniValue, Serializable {

    private static final long serialVersionUID = 1L;


    private final StoredValueDefinition definition;
    
    private final boolean isNull;

    private final ByteString content;


    public StoredValue() {
        this(true, ByteString.empty());
    }

    public StoredValue(ByteString content) {
        this(false, content);
    }

    public StoredValue(boolean isNull, ByteString content) {
        this(DefaultValueInterpreter.DEFAULT_DEFINITION, isNull, content);
    }
    
    public StoredValue(MiniValueDefinition definition, boolean isNull, ByteString content) {
        this.definition = StoredValueDefinition.of(definition);
        this.isNull = isNull;
        this.content = content;
    }

    public static StoredValue of(MiniValue value) {
        if (value.isLob()) {
            throw new IllegalArgumentException("LOB value can not be stored");
        }
        return new StoredValue(value.isNull(), value.content());
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
    public boolean isLob() {
        return false;
    }

    @Override
    public long length() {
        return content.length();
    }

    @Override
    public ByteString content() {
        return content;
    }

    @Override
    public MiniLobAccess lobAccess() throws IOException {
        return new StoredLobAccess(content);
    }

}
