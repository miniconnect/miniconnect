package hu.webarticum.miniconnect.record.translator;

import java.time.Instant;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class TimestampTranslator implements ValueTranslator {

    private static final TimestampTranslator INSTANCE = new TimestampTranslator();
    
    
    private TimestampTranslator() {
        // singleton
    }
    
    public static TimestampTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public int length() {
        return Long.BYTES * 2;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long secondsSinceEpoch = reader.readLong();
        long nanoOfSecond = reader.readLong();
        return Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        Instant instantValue = (Instant) value;
        ByteString.Builder builder = ByteString.builder();
        builder.appendLong(instantValue.getEpochSecond());
        builder.appendLong(instantValue.getNano());
        ByteString bytes = builder.build();
        return new StoredContentAccess(bytes);
    }
    
}
