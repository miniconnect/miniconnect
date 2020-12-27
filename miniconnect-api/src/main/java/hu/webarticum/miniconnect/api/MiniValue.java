package hu.webarticum.miniconnect.api;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

public interface MiniValue {

    // TODO: simplified, client-server serializable version
    // public int contentLength();
    // public byte[] content();
    // public InputStream inputStream();
    
    public boolean isNull();

    public boolean asBoolean();
    
    public byte asByte();
    
    public short asShort();
    
    public int asInt();
    
    public long asLong();
    
    public float asFloat();
    
    public double asDouble();
    
    public char asChar();

    public String asString();
    
    public BigInteger asBigInteger();
    
    public BigDecimal asBigDecimal();
    
    public Temporal asTemporal();
    
    public byte[] asByteArray();

    public CharSequence asCharSequence();
    
    public Supplier<InputStream> asInputStreamSupplier(); // FIXME ???
    
    // TODO: custom/native type?
    // TODO: tuplet, array, JSON, XML, geospatial etc.?
    
}
