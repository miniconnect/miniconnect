package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;


public class ToByteStringConverter implements TypedConverter<ByteString> {

    @Override
    public Class<ByteString> targetClazz() {
        return ByteString.class;
    }

    @Override
    public ByteString convert(Object source) {
        if (source instanceof ByteString) {
            return (ByteString) source;
        } else if (source instanceof String) {
            return ByteString.of((String) source);
        } else if (source instanceof BlobValue) {
            MiniContentAccess contentAccess = ((BlobValue) source).contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large BLOB");
            }
            return contentAccess.get();
        } else if (source instanceof ClobValue) {
            MiniContentAccess contentAccess = ((ClobValue) source).contentAccess();
            if (contentAccess.isLarge()) {
                throw new IllegalArgumentException("Too large CLOB");
            }
            return contentAccess.get();
        } else if (source instanceof Boolean) {
            return ByteString.ofByte((boolean) source ? (byte) 1 : (byte) 0);
        } else if (source instanceof Byte) {
            return ByteString.ofByte((Byte) source);
        } else if (source instanceof Short) {
            return ByteString.ofShort((Short) source);
        } else if (source instanceof Integer) {
            return ByteString.ofInt((Integer) source);
        } else if (source instanceof Long) {
            return ByteString.ofLong((Long) source);
        } else if (source instanceof LargeInteger) {
            return ByteString.of(((LargeInteger) source).toByteArray());
        } else if (source instanceof BigInteger) {
            return ByteString.of(((BigInteger) source).toByteArray());
        } else if (source instanceof LocalDate) {
            return ByteString.ofFloat((Float) source);
        } else if (source instanceof Double) {
            return ByteString.ofDouble((Double) source);
        } else if (source instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) source;
            return ByteString.builder().appendInt(bigDecimal.scale()).append(bigDecimal.unscaledValue().toByteArray()).build();
        } else if (source instanceof LocalDate) {
            return ByteString.ofLong(((LocalDate) source).toEpochDay());
        } else if (source instanceof LocalTime) {
            return ByteString.ofLong(((LocalTime) source).toNanoOfDay());
        } else if (source instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) source;
            return ByteString.builder()
                    .appendLong(localDateTime.toLocalDate().toEpochDay())
                    .appendLong(localDateTime.toLocalTime().toNanoOfDay())
                    .build();
        } else if (source instanceof OffsetTime) {
            OffsetTime offsetTime = (OffsetTime) source;
            return ByteString.builder()
                    .appendLong(offsetTime.toLocalTime().toNanoOfDay())
                    .appendInt(offsetTime.getOffset().getTotalSeconds())
                    .build();
        } else if (source instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) source;
            LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
            return ByteString.builder()
                    .appendLong(localDateTime.toLocalDate().toEpochDay())
                    .appendLong(localDateTime.toLocalTime().toNanoOfDay())
                    .appendInt(offsetDateTime.getOffset().getTotalSeconds())
                    .build();
        } else if (source instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTimeValue = (ZonedDateTime) source;
            LocalDateTime localDateTime = zonedDateTimeValue.toLocalDateTime();
            return ByteString.builder()
                    .appendLong(localDateTime.toLocalDate().toEpochDay())
                    .appendLong(localDateTime.toLocalTime().toNanoOfDay())
                    .appendInt(zonedDateTimeValue.getOffset().getTotalSeconds())
                    .append(zonedDateTimeValue.getZone().getId().getBytes(StandardCharsets.UTF_8))
                    .build();
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toInstant());
        } else if (source instanceof Instant) {
            Instant instant = (Instant) source;
            return ByteString.builder().appendLong(instant.getEpochSecond()).appendInt(instant.getNano()).build();
        } else if (source instanceof ZoneOffset) {
            return ByteString.ofInt(((ZoneOffset) source).getTotalSeconds());
        } else if (source instanceof DateTimeDelta) {
            DateTimeDelta delta = (DateTimeDelta) source;
            Period period = delta.getPeriod();
            Duration duration = delta.getDuration();
            return ByteString.builder()
                    .appendInt(period.getYears())
                    .appendInt(period.getMonths())
                    .appendInt(period.getDays())
                    .appendLong(duration.getSeconds())
                    .appendInt(duration.getNano())
                    .build();
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return ByteString.of(source.toString());
        }
    }

}
