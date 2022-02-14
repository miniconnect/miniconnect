package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class CharTranslator implements ValueTranslator {

    private static final CharTranslator INSTANCE = new CharTranslator();
    
    
    private CharTranslator() {
        // singleton
    }
    
    public static CharTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return Character.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Character.BYTES) {
            return Character.valueOf('\0');
        }
        
        return contentAccess.get(0, Character.BYTES).asBuffer().getChar();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofChar((Character) value);
        return new StoredContentAccess(bytes);
    }
    
}
