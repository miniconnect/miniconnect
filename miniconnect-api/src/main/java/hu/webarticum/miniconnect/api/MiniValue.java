package hu.webarticum.miniconnect.api;

import java.io.InputStream;

import hu.webarticum.miniconnect.util.data.ByteString;

// FIXME: close lob on fetch next
public interface MiniValue {

    public boolean isNull();

    public boolean isLarge();

    public long length();

    public ByteString content();

    public ByteString part(long start, int length);

    public InputStream inputStream();

}
