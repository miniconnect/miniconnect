package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class ByteTranslator implements ValueTranslator {

    private static final String NAME = StandardValueType.BYTE.name();

    private static final ByteTranslator INSTANCE = new ByteTranslator();
    
    
    private ByteTranslator() {
        // singleton
    }
    
    public static ByteTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Byte.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        if (contentAccess.length() < Byte.BYTES) {
            return Byte.valueOf((byte) 0);
        }
        
        return contentAccess.get(0, Byte.BYTES).asBuffer().get();
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.ofByte((Byte) value);
        return new StoredContentAccess(bytes);
    }
    
}
