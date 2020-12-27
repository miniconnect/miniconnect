package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniValue;

public class LongValue implements MiniValue {

    private final long value;
    

    public LongValue(short value) {
        this.value = value;
    }

    public LongValue(byte value) {
        this.value = Byte.toUnsignedLong(value);
    }

    public LongValue(char value) {
        this.value = Character.getNumericValue(value);
    }

    public LongValue(int value) {
        this.value = value;
    }
    
    public LongValue(long value) {
        this.value = value;
    }
    

    @Override
    public boolean isNull() {
        return false;
    }
    
    @Override
    public boolean asBoolean() {
        return (value != 0L);
    }

    @Override
    public byte asByte() {
        byte result = (byte) value;
        if (((int) result) != value) {
            throw new IllegalArgumentException("Integer overflow (long to byte)");
        }
        
        return result;
    }

    @Override
    public short asShort() {
        short result = (short) value;
        if (((int) result) != value) {
            throw new IllegalArgumentException("Integer overflow (long to short)");
        }
        
        return result;
    }

    @Override
    public int asInt() {
        return Math.toIntExact(value);
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public char asChar() {
        throw new UnsupportedOperationException("Can not represented as char");
    }

    @Override
    public String asString() {
        return Long.toString(value);
    }

    @Override
    public BigInteger asBigInteger() {
        return BigInteger.valueOf(value);
    }

    @Override
    public BigDecimal asBigDecimal() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public Temporal asTemporal() {
        throw new UnsupportedOperationException("Can not represented as Temporal");
    }

    @Override
    public byte[] asByteArray() {
        return asBigInteger().toByteArray();
    }

    @Override
    public CharSequence asCharSequence() {
        return asString();
    }

    @Override
    public Supplier<InputStream> asInputStreamSupplier() {
        return () -> new ByteArrayInputStream(asByteArray());
    }
    
}
