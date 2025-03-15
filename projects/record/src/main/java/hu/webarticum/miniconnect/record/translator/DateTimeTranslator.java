package hu.webarticum.miniconnect.record.translator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class DateTimeTranslator implements ValueTranslator {

    public static final String NAME = "DATETIME"; // NOSONAR same name is OK


    private static final DateTimeTranslator INSTANCE = new DateTimeTranslator();
    
    
    private DateTimeTranslator() {
        // singleton
    }
    
    public static DateTimeTranslator instance() {
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
        long daysSinceEpoch = reader.readLong();
        long nanoOfDay = reader.readLong();
        return LocalDateTime.of(
                LocalDate.ofEpochDay(daysSinceEpoch),
                LocalTime.ofNanoOfDay(nanoOfDay));
    }

    @Override
    public MiniContentAccess encode(Object value) {
        LocalDateTime localDateTimeValue = (LocalDateTime) value;
        ByteString.Builder builder = ByteString.builder();
        builder.appendLong(localDateTimeValue.toLocalDate().toEpochDay());
        builder.appendLong(localDateTimeValue.toLocalTime().toNanoOfDay());
        ByteString bytes = builder.build();
        return new StoredContentAccess(bytes);
    }

    @Override
    public String assuredClazzName() {
        return LocalDateTime.class.getName();
    }
    
}
