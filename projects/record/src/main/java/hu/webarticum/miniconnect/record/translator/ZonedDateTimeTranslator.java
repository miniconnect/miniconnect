package hu.webarticum.miniconnect.record.translator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class ZonedDateTimeTranslator implements ValueTranslator {

    public static final String NAME = "ZONEDDATETIME"; // NOSONAR same name is OK


    private static final ZonedDateTimeTranslator INSTANCE = new ZonedDateTimeTranslator();


    private ZonedDateTimeTranslator() {
        // singleton
    }

    public static ZonedDateTimeTranslator instance() {
        return INSTANCE;
    }


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_LENGTH;
    }

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        long daysSinceEpoch = reader.readLong();
        long nanoOfDay = reader.readLong();
        int offsetSeconds = reader.readInt();
        String zoneName = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        LocalDateTime localDateTimeValue = LocalDateTime.of(
                LocalDate.ofEpochDay(daysSinceEpoch),
                LocalTime.ofNanoOfDay(nanoOfDay));
        ZoneOffset offsetValue = ZoneOffset.ofTotalSeconds(offsetSeconds);
        ZoneId zoneIdValue = ZoneId.of(zoneName);
        return ZonedDateTime.ofLocal(localDateTimeValue, zoneIdValue, offsetValue);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ZonedDateTime zonedDateTimeValue = (ZonedDateTime) value;
        LocalDateTime localDateTime = zonedDateTimeValue.toLocalDateTime();
        long daysSinceEpoch = localDateTime.toLocalDate().toEpochDay();
        long nanoOfDay = localDateTime.toLocalTime().toNanoOfDay();
        int offsetSeconds = zonedDateTimeValue.getOffset().getTotalSeconds();
        String zoneName = zonedDateTimeValue.getZone().getId();
        ByteString bytes = ByteString.builder()
                .appendLong(daysSinceEpoch)
                .appendLong(nanoOfDay)
                .appendInt(offsetSeconds)
                .append(zoneName.getBytes(StandardCharsets.UTF_8))
                .build();
        return StoredContentAccess.of(bytes);
    }

    @Override
    public String assuredClazzName() {
        return ZonedDateTime.class.getName();
    }

}
