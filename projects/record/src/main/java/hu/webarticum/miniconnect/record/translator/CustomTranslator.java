package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class CustomTranslator implements ValueTranslator {

    private CustomTranslator(/* ... */) {
        
    }

    public static CustomTranslator of(ImmutableMap<String, ByteString> properties) {
        return new CustomTranslator();
    }
    

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        
        // TODO
        return null;
        
    }

    @Override
    public MiniContentAccess encode(Object value) {

        // TODO
        return new StoredContentAccess(ByteString.empty());
        
    }
    
}
