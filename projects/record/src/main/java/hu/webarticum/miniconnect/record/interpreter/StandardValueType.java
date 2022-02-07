package hu.webarticum.miniconnect.record.interpreter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import hu.webarticum.miniconnect.lang.ByteString;

public enum StandardValueType {

    NULL(Void.class),
    
    BOOL(Boolean.class),
    
    BYTE(Byte.class),
    
    CHAR(Character.class),
    
    SHORT(Short.class),
    
    INT(Integer.class),
    
    LONG(Long.class),
    
    FLOAT(Float.class),
    
    DOUBLE(Double.class),
    
    BIGINT(BigInteger.class),
    
    DECIMAL(BigDecimal.class),
    
    BINARY(ByteString.class),
    
    STRING(String.class),
    
    TIME(LocalTime.class),
    
    DATE(LocalDate.class),
    
    TIMESTAMP(Instant.class),
    
    COMPLEX(ComplexValue.class),
    
    JAVA(Serializable.class),
    
    // TODO: blob, clob
    
    ;
    
    
    private final Class<?> clazz;
    
    
    private StandardValueType(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    
    public Class<?> clazz() {
        return clazz;
    }
    
}
