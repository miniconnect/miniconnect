package hu.webarticum.miniconnect.record.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.customvalue.CustomValue;
import hu.webarticum.miniconnect.record.translator.BigintTranslator;
import hu.webarticum.miniconnect.record.translator.BinaryTranslator;
import hu.webarticum.miniconnect.record.translator.BoolTranslator;
import hu.webarticum.miniconnect.record.translator.ByteTranslator;
import hu.webarticum.miniconnect.record.translator.CharTranslator;
import hu.webarticum.miniconnect.record.translator.CustomTranslator;
import hu.webarticum.miniconnect.record.translator.DateTranslator;
import hu.webarticum.miniconnect.record.translator.DecimalTranslator;
import hu.webarticum.miniconnect.record.translator.DoubleTranslator;
import hu.webarticum.miniconnect.record.translator.FloatTranslator;
import hu.webarticum.miniconnect.record.translator.IntTranslator;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.LongTranslator;
import hu.webarticum.miniconnect.record.translator.NullTranslator;
import hu.webarticum.miniconnect.record.translator.ShortTranslator;
import hu.webarticum.miniconnect.record.translator.StringTranslator;
import hu.webarticum.miniconnect.record.translator.TimeTranslator;
import hu.webarticum.miniconnect.record.translator.TimestampTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public enum StandardValueType implements ValueType {

    NULL(Void.class, NullTranslator.instance()),
    
    BOOL(Boolean.class, BoolTranslator.instance()),
    
    BYTE(Byte.class, ByteTranslator.instance()),
    
    CHAR(Character.class, CharTranslator.instance()),
    
    SHORT(Short.class, ShortTranslator.instance()),
    
    INT(Integer.class, IntTranslator.instance()),
    
    LONG(Long.class, LongTranslator.instance()),
    
    FLOAT(Float.class, FloatTranslator.instance()),
    
    DOUBLE(Double.class, DoubleTranslator.instance()),
    
    BIGINT(BigInteger.class, BigintTranslator.instance()),
    
    DECIMAL(BigDecimal.class, DecimalTranslator.instance()),
    
    BINARY(ByteString.class, BinaryTranslator.instance()),
    
    STRING(String.class, StringTranslator::of),
    
    TIME(LocalTime.class, TimeTranslator.instance()),
    
    DATE(LocalDate.class, DateTranslator.instance()),
    
    TIMESTAMP(Instant.class, TimestampTranslator.instance()),
    
    CUSTOM(CustomValue.class, CustomTranslator::of),
    
    JAVA(Serializable.class, JavaTranslator.instance()),
    
    // TODO: blob, clob
    
    ;
    
    
    private final Class<?> clazz;
    
    private final Function<ImmutableMap<String, ByteString>, ValueTranslator> translatorProvider;
    
    
    private StandardValueType(Class<?> clazz, ValueTranslator translator) {
        this(clazz, p -> translator);
    }
    
    private StandardValueType(
            Class<?> clazz,
            Function<ImmutableMap<String, ByteString>, ValueTranslator> translatorProvider) {
        this.clazz = clazz;
        this.translatorProvider = translatorProvider;
    }
    
    public static StandardValueType forClazz(Class<?> clazz) {
        for (StandardValueType member : values()) {
            if (member.clazz == clazz) {
                return member;
            }
        }
        throw new IllegalArgumentException("Unsupported class: " + clazz);
    }
    
    
    @Override
    public Class<?> clazz() {
        return clazz;
    }

    @Override
    public ValueTranslator translatorFor(ImmutableMap<String, ByteString> properties) {
        return translatorProvider.apply(properties);
    }
    
}
