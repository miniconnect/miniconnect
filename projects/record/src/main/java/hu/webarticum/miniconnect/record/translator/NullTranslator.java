package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class NullTranslator implements ValueTranslator {

    private static final NullTranslator INSTANCE = new NullTranslator();
    
    
    private NullTranslator() {
        // singleton
    }
    
    public static NullTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return 0;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return null;
    }

    @Override
    public MiniContentAccess encode(Object value) {
        return new StoredContentAccess(ByteString.empty());
    }
    
}
