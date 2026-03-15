package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ToZoneOffsetConverter implements TypedConverter<ZoneOffset> {

    @Override
    public Class<ZoneOffset> targetClazz() {
        return ZoneOffset.class;
    }

    @Override
    public ZoneOffset convert(Object source) {
        if (source instanceof ZoneOffset) {
            return (ZoneOffset) source;
        } else if (source instanceof OffsetTime) {
            return ((OffsetTime) source).getOffset();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).getOffset();
        } else if (source instanceof ZonedDateTime) {
            return ((ZonedDateTime) source).getOffset();
        } else if (source instanceof Temporal) {
            Temporal temporal = (Temporal) source;
            if (temporal.isSupported(ChronoField.OFFSET_SECONDS)) {
                return ZoneOffset.ofTotalSeconds(temporal.get(ChronoField.OFFSET_SECONDS));
            } else {
                return ZoneOffset.UTC;
            }
        } else if (source instanceof Number) {
            return ZoneOffset.ofTotalSeconds(((Number) source).intValue());
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            return ZoneOffset.ofTotalSeconds(((ByteString) source).reader().readInt());
        } else if (source instanceof BlobValue) {
            return ZoneOffset.ofTotalSeconds(((BlobValue) source).contentAccess().get().reader().readInt());
        } else if (source instanceof String) {
            return ZoneOffset.of((String) source);
        } else if (source instanceof ClobValue) {
            return ZoneOffset.of(((ClobValue) source).toString());
        } else if (source instanceof DateTimeDelta) {
            return ZoneOffset.ofTotalSeconds((int) ((DateTimeDelta) source).toCollapsedDuration().getSeconds());
        } else if (source instanceof Duration) {
            return ZoneOffset.ofTotalSeconds((int) ((Duration) source).getSeconds());
        } else if (source instanceof Boolean) {
            return (Boolean) source ? ZoneOffset.ofHours(1) : ZoneOffset.UTC;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
