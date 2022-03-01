package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class BlobTranslator implements ValueTranslator {

    public static final String NAME = "BLOB"; // NOSONAR same name is OK
    

    private static final BlobTranslator INSTANCE = new BlobTranslator();
    
    
    private BlobTranslator() {
        // singleton
    }
    
    public static BlobTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_LENGTH;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return BlobValue.of(contentAccess);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        BlobValue blobValue = (BlobValue) value;
        return blobValue.contentAccess();
    }

    @Override
    public String assuredClazzName() {
        return BlobValue.class.getName();
    }
    
}
