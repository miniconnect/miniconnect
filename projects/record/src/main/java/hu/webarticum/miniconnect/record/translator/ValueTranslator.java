package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredValue;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public interface ValueTranslator {

    public String name();

    public int length();

    public Object decode(MiniContentAccess contentAccess);

    public MiniContentAccess encode(Object value);

    public default String assuredClazzName() {
        return Object.class.getName();
    }
    
    public default ImmutableMap<String, ByteString> properties() {
        return ImmutableMap.empty();
    }

    public default MiniValueDefinition definition() {
        return new StoredValueDefinition(name(), length(), properties());
    }
    
    public default MiniValue encodeFully(Object value) {
        ImmutableMap<String, ByteString> properties = properties();
        boolean isNull = (value == null);
        MiniContentAccess contentAccess = isNull ? new StoredContentAccess(ByteString.empty()) : encode(value);
        MiniValueDefinition valueDefinition = new StoredValueDefinition(name(), length(), properties);
        return new StoredValue(valueDefinition, isNull, contentAccess);
    }
    
}
