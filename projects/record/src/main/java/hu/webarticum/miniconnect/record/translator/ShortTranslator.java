package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class ShortTranslator implements ValueTranslator {

    public static final String NAME = StandardValueType.SHORT.name(); // NOSONAR same name is OK


    private static final ShortTranslator INSTANCE = new ShortTranslator();
    
    
    private ShortTranslator() {
        // singleton
    }
    
    public static ShortTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Short.BYTES;
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
