package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import hu.webarticum.miniconnect.record.converter.UnsupportedConversionException;
import hu.webarticum.miniconnect.record.converter.typed.TypedConverter;
import hu.webarticum.miniconnect.record.util.Numbers;

public class ToLocalDateTimeConverter implements TypedConverter<LocalDateTime> {

    @Override
    public Class<LocalDateTime> targetClazz() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime convert(Object source) {
        if (source instanceof LocalDateTime) {
            return (LocalDateTime) source;
        } else if (source instanceof LocalDate) {
            return ((LocalDate) source).atStartOfDay();
        } else if (source instanceof OffsetDateTime) {
            return ((OffsetDateTime) source).toLocalDateTime();
        } else if (source instanceof Timestamp) {
            return convert(((Timestamp) source).toLocalDateTime());
        } else if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneOffset.UTC);
        } else if (source instanceof Number) {
            BigDecimal bigDecimalValue = Numbers.toBigDecimal((Number) source, 9);
            long secondsSinceEpoch = bigDecimalValue.toBigInteger().longValueExact();
            int nanoOfSecond = bigDecimalValue.remainder(BigDecimal.ONE).unscaledValue().intValue();
            Instant instantValue = Instant.ofEpochSecond(secondsSinceEpoch, nanoOfSecond);
            return instantValue.atOffset(ZoneOffset.UTC).toLocalDateTime();
        } else if (source instanceof String) {
            String dateTimeString = (String) source;
            if (dateTimeString.indexOf('Z', 16) >= 0 || dateTimeString.indexOf('+', 16) >= 0 || dateTimeString.indexOf('-', 16) >= 0) {
                return OffsetDateTime.parse(dateTimeString).toLocalDateTime();
            } else {
                return LocalDateTime.parse(dateTimeString);
            }
        } else {
            throw new UnsupportedConversionException(source, targetClazz());
        }
    }
    
}
