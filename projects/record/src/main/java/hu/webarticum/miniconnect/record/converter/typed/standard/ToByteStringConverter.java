package hu.webarticum.miniconnect.record.converter.typed.standard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
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
        } else if (source instanceof CustomValue) {
            return convert(((CustomValue) source).get());
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
        } else if (source instanceof LocalDate) {
            return ByteString.of(((LocalDate) source).format(DateTimeFormatter.ISO_DATE));
        } else if (source instanceof LocalTime) {
            return ByteString.of(((LocalTime) source).format(DateTimeFormatter.ISO_DATE));
        } else if (source instanceof LocalDateTime) {
            return ByteString.of(((LocalDateTime) source).format(DateTimeFormatter.ISO_DATE));
        } else if (source instanceof Instant) {
            return ByteString.of(((Instant) source).toString());
        } else {
            return ByteString.of(source.toString());
        }
    }

}
