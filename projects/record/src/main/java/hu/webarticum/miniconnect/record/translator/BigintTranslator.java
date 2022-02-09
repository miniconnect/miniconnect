package hu.webarticum.miniconnect.record.translator;

import java.math.BigInteger;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class BigintTranslator implements ValueTranslator {

    private static final BigintTranslator INSTANCE = new BigintTranslator();
    
    
    private BigintTranslator() {
        // singleton
    }
    
    public static BigintTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return new BigInteger(contentAccess.get().extract());
    }

    @Override
    public MiniContentAccess encode(Object value) {
        BigInteger bigIntegerValue = (BigInteger) value;
        ByteString bytes = ByteString.wrap(bigIntegerValue.toByteArray());
        return new StoredContentAccess(bytes);
    }
    
}
