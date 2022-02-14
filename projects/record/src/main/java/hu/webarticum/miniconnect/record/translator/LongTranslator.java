package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class LongTranslator implements ValueTranslator {

    private static final LongTranslator INSTANCE = new LongTranslator();
    
    
    private LongTranslator() {
        // singleton
    }
    
    public static LongTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return Long.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Long.BYTES) {
            return Long.valueOf(0L);
        }
        
        return contentAccess.get(0, Long.BYTES).asBuffer().getLong();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofLong((Long) value);
        return new StoredContentAccess(bytes);
    }
    
}
