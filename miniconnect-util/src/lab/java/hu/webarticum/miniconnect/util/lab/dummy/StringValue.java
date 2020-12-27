package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniValue;

public class StringValue implements MiniValue {
    
    private static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    
    private static final Pattern TRUE_PATTERN = Pattern.compile(
            "1|t(?:rue)?|y(?:es)?", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern FALSE_PATTERN = Pattern.compile(
            "0|f(?:alse)?|n(?:o)?", Pattern.CASE_INSENSITIVE);
    
    
    private final String value;
    
    private final Charset outputCharset;
    
    
    public StringValue(String value) {
        this(value, DEFAULT_CHARSET);
    }

    public StringValue(String value, Charset outputCharset) {
        this.value = value;
        this.outputCharset = outputCharset;
    }


    @Override
    public boolean isNull() {
        return false;
    }
    
    @Override
    public boolean asBoolean() {
        if (TRUE_PATTERN.matcher(value).matches()) {
            return true;
        } else if (FALSE_PATTERN.matcher(value).matches()) {
            return false;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Value can not be interpreted as boolean: %s",
                    value));
        }
    }

    @Override
    public byte asByte() {
        byte[] bytes = asByteArray();
        if (bytes.length != 1) {
            throw new IllegalArgumentException("Content is not a single byte");
        }
        
        return bytes[0];
    }

    @Override
    public short asShort() {
        return Short.parseShort(value);
    }

    @Override
    public int asInt() {
        return Integer.parseInt(value);
    }

    @Override
    public long asLong() {
        return Long.parseLong(value);
    }

    @Override
    public strictfp float asFloat() {
        return Float.parseFloat(value);
    }

    @Override
    public strictfp double asDouble() {
        return Double.parseDouble(value);
    }

    @Override
    public char asChar() {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Value is not a single character");
        }
        
        return value.charAt(0);
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public BigInteger asBigInteger() {
        return new BigInteger(value);
    }

    @Override
    public BigDecimal asBigDecimal() {
        return new BigDecimal(value);
    }

    @Override
    public Temporal asTemporal() {
        throw new UnsupportedOperationException("Parsing as Temporal is not supported");
    }

    @Override
    public byte[] asByteArray() {
        return value.getBytes(outputCharset);
    }

    @Override
    public CharSequence asCharSequence() {
        return value;
    }

    @Override
    public Supplier<InputStream> asInputStreamSupplier() {
        return () -> new ByteArrayInputStream(asByteArray());
    }

}
