package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniValue;

public class NullValue implements MiniValue {

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public byte asByte() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public short asShort() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public int asInt() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public long asLong() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public float asFloat() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public double asDouble() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public char asChar() {
        throw new NullPointerException("Value is null");
    }

    // FIXME: empty string?
    @Override
    public String asString() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public BigInteger asBigInteger() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new NullPointerException("Value is null");
    }

    @Override
    public Temporal asTemporal() {
        throw new NullPointerException("Value is null");
    }

    // FIXME: empty array?
    @Override
    public byte[] asByteArray() {
        throw new NullPointerException("Value is null");
    }

    // FIXME: empty string?
    @Override
    public CharSequence asCharSequence() {
        throw new NullPointerException("Value is null");
    }

    // FIXME: empty stream?
    @Override
    public Supplier<InputStream> asInputStreamSupplier() {
        throw new NullPointerException("Value is null");
    }

}
