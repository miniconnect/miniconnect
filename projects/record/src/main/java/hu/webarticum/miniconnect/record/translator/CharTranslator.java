package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class CharTranslator implements ValueTranslator {

    public static final String NAME = "CHAR"; // NOSONAR same name is OK
    

    private static final CharTranslator INSTANCE = new CharTranslator();
    
    
    private CharTranslator() {
        // singleton
    }
    
    public static CharTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
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

    @Override
    public String assuredClazzName() {
        return Character.class.getName();
    }
    
}
