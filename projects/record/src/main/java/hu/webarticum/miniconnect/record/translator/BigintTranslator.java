package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.LargeInteger;

public class BigintTranslator implements ValueTranslator {
    
    public static final String NAME = "BIGINT"; // NOSONAR same name is OK

    
    private static final BigintTranslator INSTANCE = new BigintTranslator();
    
    
    private BigintTranslator() {
        // singleton
    }
    
    public static BigintTranslator instance() {
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
        return LargeInteger.of(contentAccess.get().extract());
    }

    @Override
    public MiniContentAccess encode(Object value) {
        LargeInteger largeIntegerValue = (LargeInteger) value;
        ByteString bytes = ByteString.wrap(largeIntegerValue.toByteArray());
        return new StoredContentAccess(bytes);
    }

    @Override
    public String assuredClazzName() {
        return LargeInteger.class.getName();
    }
    
}
