package hu.webarticum.miniconnect.record.translator;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class OffsetTimeTranslator implements ValueTranslator {

    public static final String NAME = "OFFSETTIME"; // NOSONAR same name is OK


    private static final OffsetTimeTranslator INSTANCE = new OffsetTimeTranslator();
    
    
    private OffsetTimeTranslator() {
        // singleton
    }
    
    public static OffsetTimeTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return Long.BYTES + Integer.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long nanoOfDay = reader.readLong();
        int offsetSeconds = reader.readInt();
        return OffsetTime.of(
                LocalTime.ofNanoOfDay(nanoOfDay),
                ZoneOffset.ofTotalSeconds(offsetSeconds));
    }

    @Override
    public MiniContentAccess encode(Object value) {
        OffsetTime offsetTimeValue = (OffsetTime) value;
        long nanoOfDay = offsetTimeValue.toLocalTime().toNanoOfDay();
        int offsetSeconds = offsetTimeValue.getOffset().getTotalSeconds();
        ByteString bytes = ByteString.builder()
                .appendLong(nanoOfDay)
                .appendInt(offsetSeconds)
                .build();
        return new StoredContentAccess(bytes);
    }

    @Override
    public String assuredClazzName() {
        return OffsetTime.class.getName();
    }
    
}
