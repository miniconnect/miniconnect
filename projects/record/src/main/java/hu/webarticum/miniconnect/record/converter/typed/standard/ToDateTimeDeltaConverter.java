package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ToDateTimeDeltaConverter implements TypedConverter<DateTimeDelta> {

    @Override
    public Class<DateTimeDelta> targetClazz() {
        return DateTimeDelta.class;
    }

    @Override
    public DateTimeDelta convert(Object source) {
        if (source instanceof DateTimeDelta) {
            return (DateTimeDelta) source;
        } else if (source instanceof Duration) {
            return DateTimeDelta.of((Duration) source);
        } else if (source instanceof Period) {
            return DateTimeDelta.of((Period) source);
        } else if (source instanceof Number) {
            long seconds;
            int nanos;
            if (
                    source instanceof LargeInteger ||
                    source instanceof BigInteger ||
                    source instanceof Long ||
                    source instanceof Integer ||
                    source instanceof Short ||
                    source instanceof Byte) {
                seconds = ((Number) source).longValue();
                nanos = 0;
            } else if (source instanceof BigDecimal) {
                BigDecimal bigDecimalSeconds = (BigDecimal) source;
                seconds = bigDecimalSeconds.toBigInteger().longValueExact();
                nanos = bigDecimalSeconds.setScale(9, RoundingMode.HALF_UP).remainder(BigDecimal.ONE).unscaledValue().intValue();
            } else {
                double doubleSeconds = ((Number) source).doubleValue();
                seconds = (long) doubleSeconds;
                nanos = (int) ((doubleSeconds - seconds) * 1_000_000_000.0);
            }
            return DateTimeDelta.of(0, 0, 0, seconds, nanos);
        } else if (source instanceof Temporal) {
            return DateTimeDelta.between(LocalDate.ofEpochDay(0).atStartOfDay(ZoneOffset.UTC), (Temporal) source);
        } else if (source instanceof BitString) {
            return convert(new ToLargeIntegerConverter().convert(source));
        } else if (source instanceof ByteString) {
            ByteString.Reader reader = ((ByteString) source).reader();
            int years = reader.readInt();
            int months = reader.readInt();
            int days = reader.readInt();
            long seconds = reader.readLong();
            int nanos = reader.readInt();
            return DateTimeDelta.of(years, months, days, seconds, nanos);
        } else if (source instanceof BlobValue) {
            return convert(((BlobValue) source).contentAccess().get());
        } else if (source instanceof String) {
            return DateTimeDelta.parse((String) source);
        } else if (source instanceof ClobValue) {
            return DateTimeDelta.parse(((ClobValue) source).toString());
        } else if (source instanceof Boolean) {
            return (Boolean) source ? DateTimeDelta.of(0, 0, 0, 1, 0) : DateTimeDelta.ZERO;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }

}
