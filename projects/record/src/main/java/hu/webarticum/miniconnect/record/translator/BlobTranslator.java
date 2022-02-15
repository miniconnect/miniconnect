package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class BlobTranslator implements ValueTranslator {

    private static final BlobTranslator INSTANCE = new BlobTranslator();
    
    
    private BlobTranslator() {
        // singleton
    }
    
    public static BlobTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_SIZE;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return new BlobValue(contentAccess);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        BlobValue blobValue = (BlobValue) value;
        return blobValue.contentAccess();
    }
    
}
