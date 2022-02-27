package hu.webarticum.miniconnect.record.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;
import hu.webarticum.miniconnect.record.translator.BigintTranslator;
import hu.webarticum.miniconnect.record.translator.BinaryTranslator;
import hu.webarticum.miniconnect.record.translator.BlobTranslator;
import hu.webarticum.miniconnect.record.translator.BoolTranslator;
import hu.webarticum.miniconnect.record.translator.ByteTranslator;
import hu.webarticum.miniconnect.record.translator.CharTranslator;
import hu.webarticum.miniconnect.record.translator.ClobTranslator;
import hu.webarticum.miniconnect.record.translator.CustomTranslator;
import hu.webarticum.miniconnect.record.translator.DateTranslator;
import hu.webarticum.miniconnect.record.translator.DecimalTranslator;
import hu.webarticum.miniconnect.record.translator.DoubleTranslator;
import hu.webarticum.miniconnect.record.translator.FloatTranslator;
import hu.webarticum.miniconnect.record.translator.IntTranslator;
import hu.webarticum.miniconnect.record.translator.LongTranslator;
import hu.webarticum.miniconnect.record.translator.NullTranslator;
import hu.webarticum.miniconnect.record.translator.ShortTranslator;
import hu.webarticum.miniconnect.record.translator.StringTranslator;
import hu.webarticum.miniconnect.record.translator.TimeTranslator;
import hu.webarticum.miniconnect.record.translator.TimestampTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public enum StandardValueType implements ValueType {

    NULL(ByteString.of("NUL"), Void.class, NullTranslator.instance()),
    
    BOOL(ByteString.of("BOL"), Boolean.class, BoolTranslator.instance()),
    
    BYTE(ByteString.of("BYT"), Byte.class, ByteTranslator.instance()),
    
    CHAR(ByteString.of("CHR"), Character.class, CharTranslator.instance()),
    
    SHORT(ByteString.of("SHT"), Short.class, ShortTranslator.instance()),
    
    INT(ByteString.of("INT"), Integer.class, IntTranslator.instance()),
    
    LONG(ByteString.of("LNG"), Long.class, LongTranslator.instance()),
    
    FLOAT(ByteString.of("FLT"), Float.class, FloatTranslator.instance()),
    
    DOUBLE(ByteString.of("DBL"), Double.class, DoubleTranslator.instance()),
    
    BIGINT(ByteString.of("BNT"), BigInteger.class, BigintTranslator.instance()),
    
    DECIMAL(ByteString.of("DEC"), BigDecimal.class, DecimalTranslator.instance()),
    
    BINARY(ByteString.of("BIN"), ByteString.class, BinaryTranslator.instance()),
    
    STRING(ByteString.of("STR"), String.class, StringTranslator::of),
    
    TIME(ByteString.of("TIM"), LocalTime.class, TimeTranslator.instance()),
    
    DATE(ByteString.of("DAT"), LocalDate.class, DateTranslator.instance()),
    
    TIMESTAMP(ByteString.of("TSP"), Instant.class, TimestampTranslator.instance()),
    
    BLOB(ByteString.of("BLB"), BlobValue.class, BlobTranslator.instance()),

    CLOB(ByteString.of("CLB"), ClobValue.class, ClobTranslator::of),

    CUSTOM(ByteString.of("CUS"), CustomValue.class, CustomTranslator::of),

    ;
    
    
    public static final int FLAG_LENGTH = 3;
    
    
    private final ByteString flag;
    
    private final Class<?> clazz;
    
    private final Function<ImmutableMap<String, ByteString>, ValueTranslator> translatorProvider;
    
    
    private StandardValueType(ByteString flag, Class<?> clazz, ValueTranslator translator) {
        this(flag, clazz, p -> translator);
    }
    
    private StandardValueType(
            ByteString flag,
            Class<?> clazz,
            Function<ImmutableMap<String, ByteString>, ValueTranslator> translatorProvider) {
        this.flag = flag;
        this.clazz = clazz;
        this.translatorProvider = translatorProvider;
    }

    public static StandardValueType forFlag(ByteString flag) {
        for (StandardValueType member : values()) {
            if (member.flag == flag) {
                return member;
            }
        }
        throw new IllegalArgumentException("Unknown flag: " + flag);
    }
    
    public static StandardValueType forClazz(Class<?> clazz) {
        for (StandardValueType member : values()) {
            if (member.clazz == clazz) {
                return member;
            }
        }
        throw new IllegalArgumentException("Unsupported class: " + clazz);
    }
    

    public ByteString flag() {
        return flag;
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
