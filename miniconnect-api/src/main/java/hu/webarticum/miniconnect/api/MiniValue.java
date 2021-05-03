package hu.webarticum.miniconnect.api;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.util.data.ByteString;

public interface MiniValue {
    
    public boolean isNull();

    public long length();

    public ByteString shortContent();

    public ByteString part(long start, int length) throws IOException;

    public InputStream inputStream() throws IOException;

}
