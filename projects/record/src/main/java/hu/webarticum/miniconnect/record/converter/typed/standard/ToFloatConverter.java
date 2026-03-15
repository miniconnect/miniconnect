package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class ToFloatConverter implements TypedConverter<Float> {

    @Override
    public Class<Float> targetClazz() {
        return Float.class;
    }

    @Override
    public Float convert(Object source) {
        if (source instanceof Float) {
            return ((Float) source);
        } else if (source instanceof Number) {
            return ((Number) source).floatValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1f : 0f;
        } else if (source instanceof Character) {
            return ((float) (char) source);
        } else if (source instanceof LocalDate) {
            return (float) ((LocalDate) source).toEpochDay();
        } else if (source instanceof LocalTime) {
            return ((LocalTime) source).toNanoOfDay() / 1_000_000_000f;
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            long secondsSinceEpoch = ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
            float fragmentOfSecond = ((LocalDateTime) source).getNano() / 1_000_000_000f;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof ZonedDateTime) {
            return convert(((ZonedDateTime) source).toInstant());
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            long secondsSinceEpoch = ((Instant) source).getEpochSecond();
            float fragmentOfSecond = ((Instant) source).getNano() / 1_000_000_000f;
            return secondsSinceEpoch + fragmentOfSecond;
        } else if (source instanceof ZoneOffset) {
            return (float) ((ZoneOffset) source).getTotalSeconds();
        } else if (source instanceof DateTimeDelta) {
            Duration duration = ((DateTimeDelta) source).toCollapsedDuration();
            return duration.getSeconds() + (0.000_000_001f * duration.getNano());
        } else if (source instanceof Duration) {
            Duration duration = (Duration) source;
            return duration.getSeconds() + (0.000_000_001f * duration.getNano());
        } else if (source instanceof Period) {
            Period period = (Period) source;
            return (period.getYears() * 365f) + (period.getMonths() * 30f) + (period.getDays());
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            return ((ByteString) source).reader().readFloat();
        } else if (source instanceof BlobValue) {
            return ((BlobValue) source).contentAccess().get().reader().readFloat();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new BigDecimal(source.toString()).floatValue();
        }
    }

}
