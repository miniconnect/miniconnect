package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class DoubleTranslator implements ValueTranslator {

    public static final String NAME = StandardValueType.DOUBLE.name(); // NOSONAR same name is OK


    private static final DoubleTranslator INSTANCE = new DoubleTranslator();
    
    
    private DoubleTranslator() {
        // singleton
    }
    
    public static DoubleTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Double.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Double.BYTES) {
            return Double.valueOf(0f);
        }
        
        return contentAccess.get(0, Double.BYTES).asBuffer().getDouble();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofDouble((Double) value);
        return new StoredContentAccess(bytes);
    }
    
}
