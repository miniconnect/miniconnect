package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class FloatTranslator implements ValueTranslator {

    public static final String NAME = StandardValueType.FLOAT.name(); // NOSONAR same name is OK


    private static final FloatTranslator INSTANCE = new FloatTranslator();
    
    
    private FloatTranslator() {
        // singleton
    }
    
    public static FloatTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
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
