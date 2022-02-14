package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class FloatTranslator implements ValueTranslator {

    private static final FloatTranslator INSTANCE = new FloatTranslator();
    
    
    private FloatTranslator() {
        // singleton
    }
    
    public static FloatTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return Float.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Float.BYTES) {
            return Float.valueOf(0f);
        }
        
        return contentAccess.get(0, Float.BYTES).asBuffer().getFloat();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofFloat((Float) value);
        return new StoredContentAccess(bytes);
    }
    
}
