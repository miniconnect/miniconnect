package hu.webarticum.miniconnect.record.translator;

import java.time.Duration;
import java.time.Period;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;

public class IntervalTranslator implements ValueTranslator {

    public static final String NAME = "INTERVAL"; // NOSONAR same name is OK


    private static final IntervalTranslator INSTANCE = new IntervalTranslator();


    private IntervalTranslator() {
        // singleton
    }

    public static IntervalTranslator instance() {
        return INSTANCE;
    }


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int length() {
        return Long.BYTES + (Integer.BYTES * 4);
    }

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        ByteString.Reader reader = contentAccess.get().reader();
        int years = reader.readInt();
        int months = reader.readInt();
        int days = reader.readInt();
        long seconds = reader.readLong();
        int nanos = reader.readInt();
        return DateTimeDelta.of(years, months, days, seconds, nanos);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        DateTimeDelta deltaValue = (DateTimeDelta) value;
        Period period = deltaValue.getPeriod();
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        Duration duration = deltaValue.getDuration();
        long seconds = duration.getSeconds();
        int nanos = duration.getNano();
        ByteString bytes = ByteString.builder()
                .appendInt(years)
                .appendInt(months)
                .appendInt(days)
                .appendLong(seconds)
                .appendInt(nanos)
                .build();
        return StoredContentAccess.of(bytes);
    }

    @Override
    public String assuredClazzName() {
        return DateTimeDelta.class.getName();
    }

}
