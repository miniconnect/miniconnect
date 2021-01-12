package hu.webarticum.miniconnect.util.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import hu.webarticum.miniconnect.api.MiniValue;

public class StoredValue implements MiniValue {
    
    private final boolean isNull;
    
    private final byte[] content;
    

    public StoredValue() {
        this(true, false, new byte[0]);
    }
    
    public StoredValue(byte[] content) {
        this(false, true, content);
    }
    
    public StoredValue(boolean isNull, boolean copy, byte[] content) {
        this.isNull = isNull;
        this.content = copy ? Arrays.copyOf(content, content.length) : content;
    }
    

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public int contentLength() {
        return content.length;
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }

    @Override
    public InputStream inputStream() {
        return new ByteArrayInputStream(content);
    }

}