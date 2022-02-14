package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class BinaryTranslator implements ValueTranslator {

    private static final BinaryTranslator INSTANCE = new BinaryTranslator();
    
    
    private BinaryTranslator() {
        // singleton
    }
    
    public static BinaryTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_SIZE;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return contentAccess.get();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        return new StoredContentAccess((ByteString) value);
    }
    
}
