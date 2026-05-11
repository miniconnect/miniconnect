package hu.webarticum.miniconnect.record.converter.typed.standard;

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

public class ToLongConverter implements TypedConverter<Long> {

    @Override
    public Class<Long> targetClazz() {
        return Long.class;
    }

    @Override
    public Long convert(Object source) {
        if (source instanceof Long) {
            return ((Long) source);
        } else if (source instanceof Number) {
            return ((Number) source).longValue();
        } else if (source instanceof Boolean) {
            return ((boolean) source) ? 1L : 0L;
        } else if (source instanceof BitString) {
            return ((BitString) source).toLong();
        } else if (source instanceof ByteString) {
            return ((ByteString) source).reader().readLong();
        } else if (source instanceof BlobValue) {
            return ((BlobValue) source).contentAccess().get().reader().readLong();
        } else if (source instanceof Character) {
            return ((long) (char) source);
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).toEpochDay();
        } else if (source instanceof LocalTime) {
            return (long) ((LocalTime) source).toSecondOfDay();
        } else if (source instanceof OffsetTime) {
            return convert(((OffsetTime) source).toLocalTime());
        } else if (source instanceof LocalDateTime) {
            return ((LocalDateTime) source).toEpochSecond(ZoneOffset.UTC);
        } else if (source instanceof OffsetDateTime) {
            return convert(((OffsetDateTime) source).toInstant());
        } else if (source instanceof ZonedDateTime) {
            return convert(((ZonedDateTime) source).toInstant());
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            return ((Instant) source).getEpochSecond();
        } else if (source instanceof ZoneOffset) {
            return (long) ((ZoneOffset) source).getTotalSeconds();
        } else if (source instanceof DateTimeDelta) {
            return ((DateTimeDelta) source).toCollapsedDuration().getSeconds();
        } else if (source instanceof Duration) {
            return ((Duration) source).getSeconds();
        } else if (source instanceof Period) {
            Period period = (Period) source;
            return (period.getYears() * 365L) + (period.getMonths() * 30L) + (period.getDays());
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return new ToLargeIntegerConverter().convert(source).longValue();
        }
    }

}
