package hu.webarticum.miniconnect.record.translator;

import java.time.ZoneOffset;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class TimezoneTranslator implements ValueTranslator {

    public static final String NAME = "TIMEZONE"; // NOSONAR same name is OK


    private static final TimezoneTranslator INSTANCE = new TimezoneTranslator();


    private TimezoneTranslator() {
        // singleton
    }

    public static TimezoneTranslator instance() {
        return INSTANCE;
    }


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int length() {
        return Integer.BYTES;
    }

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        int offsetSeconds = reader.readInt();
        return ZoneOffset.ofTotalSeconds(offsetSeconds);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ZoneOffset offsetValue = (ZoneOffset) value;
        int offsetSeconds = offsetValue.getTotalSeconds();
        ByteString bytes = ByteString.builder()
                .appendInt(offsetSeconds)
                .build();
        return StoredContentAccess.of(bytes);
    }

    @Override
    public String assuredClazzName() {
        return ZoneOffset.class.getName();
    }

}
