package hu.webarticum.miniconnect.tool.result;

import java.io.InputStream;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ByteString;

public class StoredValue implements MiniValue, Serializable {

    private static final long serialVersionUID = 1L;


    private final boolean isNull;

    private final ByteString content;


    public StoredValue() {
        this(true, ByteString.empty());
    }

    public StoredValue(ByteString content) {
        this(false, content);
    }

    public StoredValue(boolean isNull, ByteString content) {
        this.isNull = isNull;
        this.content = content;
    }

    public static StoredValue of(MiniValue value) {
        return new StoredValue(value.isNull(), value.content());
    }


    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public boolean isLarge() {
        return false;
    }

    @Override
    public long length() {
        return content.length();
    }

    @Override
    public ByteString content() {
        return content;
    }

    @Override
    public ByteString part(long start, int length) {
        return ByteString.wrap(content.extract((int) start, length));
    }

    @Override
    public InputStream inputStream() {
        return content.asInputStream();
    }

}
