package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.DateTimeDelta;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;

public class ToBooleanConverter implements TypedConverter<Boolean> {

    public static final Pattern FALSE_PATTERN = Pattern.compile(
            "f|false|off|n|no|disabled|0|0?\\.0+|\\s*",
            Pattern.CASE_INSENSITIVE);


    @Override
    public Class<Boolean> targetClazz() {
        return Boolean.class;
    }

    @Override
    public Boolean convert(Object source) {
        if (source instanceof Boolean) {
            return (Boolean) source;
        } else if (source instanceof LargeInteger) {
            return !((LargeInteger) source).isZero();
        } else if (source instanceof BigDecimal) {
            return ((BigDecimal) source).signum() != 0;
        } else if (source instanceof Number) {
            return ((Number) source).doubleValue() != 0d;
        } else if (source instanceof DateTimeDelta) {
            return !((DateTimeDelta) source).isZero();
        } else if (source instanceof Duration) {
            return !((Duration) source).isZero();
        } else if (source instanceof Period) {
            return !((Period) source).isZero();
        } else if (source instanceof Temporal) {
            DateTimeDelta delta = DateTimeDelta.between(LocalDate.ofEpochDay(0).atStartOfDay(ZoneOffset.UTC), (Temporal) source);
            return !delta.isZero();
        } else if (source instanceof ZoneOffset) {
            return ((ZoneOffset) source).getTotalSeconds() == 0;
        } else if (source instanceof BitString) {
            return ((BitString) source).hasZerosOnly();
        } else if (source instanceof ByteString) {
            ByteString byteStringValue = (ByteString) source;
            return !byteStringValue.isEmpty() && byteStringValue.byteAt(0) != 0;
        } else if (source instanceof BlobValue) {
            ByteString byteStringValue = ((BlobValue) source).contentAccess().get();
            return !byteStringValue.isEmpty() && byteStringValue.byteAt(0) != 0;
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
        } else {
            return !FALSE_PATTERN.matcher(source.toString()).matches();
        }
    }

}
