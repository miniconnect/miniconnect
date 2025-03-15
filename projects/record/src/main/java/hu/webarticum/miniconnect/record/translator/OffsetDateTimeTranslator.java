package hu.webarticum.miniconnect.record.translator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class OffsetDateTimeTranslator implements ValueTranslator {

    public static final String NAME = "OFFSETDATETIME"; // NOSONAR same name is OK


    private static final OffsetDateTimeTranslator INSTANCE = new OffsetDateTimeTranslator();
    
    
    private OffsetDateTimeTranslator() {
        // singleton
    }
    
    public static OffsetDateTimeTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return (Long.BYTES * 2) + Integer.BYTES;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long daysSinceEpoch = reader.readLong();
        long nanoOfDay = reader.readLong();
        int offsetSeconds = reader.readInt();
        LocalDateTime localDateTimeValue = LocalDateTime.of(
                LocalDate.ofEpochDay(daysSinceEpoch),
                LocalTime.ofNanoOfDay(nanoOfDay));
        return OffsetDateTime.of(localDateTimeValue, ZoneOffset.ofTotalSeconds(offsetSeconds));
    }

    @Override
    public MiniContentAccess encode(Object value) {
        OffsetDateTime offsetDateTimeValue = (OffsetDateTime) value;
        LocalDateTime localDateTime = offsetDateTimeValue.toLocalDateTime();
        long daysSinceEpoch = localDateTime.toLocalDate().toEpochDay();
        long nanoOfDay = localDateTime.toLocalTime().toNanoOfDay();
        int offsetSeconds = offsetDateTimeValue.getOffset().getTotalSeconds();
        ByteString bytes = ByteString.builder()
                .appendLong(daysSinceEpoch)
                .appendLong(nanoOfDay)
                .appendInt(offsetSeconds)
                .build();
        return new StoredContentAccess(bytes);
    }

    @Override
    public String assuredClazzName() {
        return LocalDateTime.class.getName();
    }
    
}
