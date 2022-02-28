package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class BlobTranslator implements ValueTranslator {

    private static final String NAME = StandardValueType.BLOB.name();

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
        return MiniValueDefinition.DYNAMIC_SIZE;
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
    
}
