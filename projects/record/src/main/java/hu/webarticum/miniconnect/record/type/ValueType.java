package hu.webarticum.miniconnect.record.type;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public interface ValueType {

    public Class<?> clazz();
    
    public ValueTranslator translatorFor(ImmutableMap<String, ByteString> properties);

    public default ValueTranslator defaultTranslator() {
        return translatorFor(ImmutableMap.empty());
    }
    
}
