package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class BoolTranslator implements ValueTranslator {

    public static final String NAME = "BOOL"; // NOSONAR same name is OK
    

    private static final BoolTranslator INSTANCE = new BoolTranslator();
    
    
    private BoolTranslator() {
        // singleton
    }
    
    public static BoolTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return 1;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return
                contentAccess.length() >= 1 &&
                contentAccess.get().byteAt(0) != (byte) 0;
    }

    @Override
    public MiniContentAccess encode(Object value) {
        byte b = (value == Boolean.TRUE) ? (byte) 1 : (byte) 0;
        ByteString bytes = ByteString.of(new byte[] { b });
        return new StoredContentAccess(bytes);
    }

    @Override
    public String assuredClazzName() {
        return Boolean.class.getName();
    }
    
}
