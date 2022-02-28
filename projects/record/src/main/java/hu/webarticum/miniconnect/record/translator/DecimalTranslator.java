package hu.webarticum.miniconnect.record.translator;

import java.math.BigDecimal;
import java.math.BigInteger;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class DecimalTranslator implements ValueTranslator {

    public static final String NAME = "DECIMAL"; // NOSONAR same name is OK


    private static final DecimalTranslator INSTANCE = new DecimalTranslator();
    
    
    private DecimalTranslator() {
        // singleton
    }
    
    public static DecimalTranslator instance() {
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
        ByteString.Reader reader = contentAccess.get().reader();
        int scale = reader.readInt();
        BigInteger bigIntegerValue = new BigInteger(reader.readRemaining());
        return new BigDecimal(bigIntegerValue, scale);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        BigDecimal bigDecimalValue = (BigDecimal) value;
        ByteString.Builder builder = ByteString.builder();
        builder.appendInt(bigDecimalValue.scale());
        builder.append(bigDecimalValue.unscaledValue().toByteArray());
        ByteString bytes = builder.build();
        return new StoredContentAccess(bytes);
    }
    
}
