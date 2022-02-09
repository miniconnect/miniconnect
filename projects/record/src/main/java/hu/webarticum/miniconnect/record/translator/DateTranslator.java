package hu.webarticum.miniconnect.record.translator;

import java.time.LocalDate;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class DateTranslator implements ValueTranslator {

    private static final DateTranslator INSTANCE = new DateTranslator();
    
    
    private DateTranslator() {
        // singleton
    }
    
    public static DateTranslator instance() {
        return INSTANCE;
    }
    

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long daysSinceEpoch = reader.readLong();
        return LocalDate.ofEpochDay(daysSinceEpoch);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        LocalDate localDateValue = (LocalDate) value;
        long daysSinceEpoch = localDateValue.toEpochDay();
        ByteString bytes = ByteString.ofLong(daysSinceEpoch);
        return new StoredContentAccess(bytes);
    }
    
}
