package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class ShortTranslator implements ValueTranslator {

    private static final ShortTranslator INSTANCE = new ShortTranslator();
    
    
    private ShortTranslator() {
        // singleton
    }
    
    public static ShortTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Short.BYTES) {
            return Short.valueOf((short) 0);
        }
        
        return contentAccess.get(0, Short.BYTES).asBuffer().getShort();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofShort((Short) value);
        return new StoredContentAccess(bytes);
    }
    
}
