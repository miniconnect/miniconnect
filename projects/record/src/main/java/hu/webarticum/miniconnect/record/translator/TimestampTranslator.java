package hu.webarticum.miniconnect.record.translator;

import java.time.Instant;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class TimestampTranslator implements ValueTranslator {

    private static final String NAME = StandardValueType.TIMESTAMP.name();

    private static final TimestampTranslator INSTANCE = new TimestampTranslator();
    
    
    private TimestampTranslator() {
        // singleton
    }
    
    public static TimestampTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Long.BYTES * 2;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long secondsSinceEpoch = reader.readLong();
        int nanoOfSecond = reader.readInt();
        return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        Instant instantValue = (Instant) value;
        ByteString.Builder builder = ByteString.builder();
        builder.appendLong(instantValue.getEpochSecond());
        builder.appendInt(instantValue.getNano());
        ByteString bytes = builder.build();
        return new StoredContentAccess(bytes);
    }
    
}
