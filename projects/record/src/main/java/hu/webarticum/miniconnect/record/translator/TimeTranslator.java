package hu.webarticum.miniconnect.record.translator;

import java.time.LocalTime;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class TimeTranslator implements ValueTranslator {

    private static final TimeTranslator INSTANCE = new TimeTranslator();
    
    
    private TimeTranslator() {
        // singleton
    }
    
    public static TimeTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long nanoOfDay = reader.readLong();
        return LocalTime.ofNanoOfDay(nanoOfDay);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        LocalTime localTimeValue = (LocalTime) value;
        long nanoOfDay = localTimeValue.toNanoOfDay();
        ByteString bytes = ByteString.ofLong(nanoOfDay);
        return new StoredContentAccess(bytes);
    }
    
}
