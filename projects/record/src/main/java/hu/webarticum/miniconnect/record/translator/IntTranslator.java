package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class IntTranslator implements ValueTranslator {

    public static final String NAME = StandardValueType.INT.name(); // NOSONAR same name is OK


    private static final IntTranslator INSTANCE = new IntTranslator();
    
    
    private IntTranslator() {
        // singleton
    }
    
    public static IntTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Integer.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Integer.BYTES) {
            return Integer.valueOf(0);
        }
        
        return contentAccess.get(0, Integer.BYTES).asBuffer().getInt();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofInt((Integer) value);
        return new StoredContentAccess(bytes);
    }
    
}
