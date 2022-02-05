package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.InputStream;

import hu.webarticum.miniconnect.lang.ByteString;

public interface MiniContentAccess extends Closeable {
    
    public long length();
    
    public boolean isLarge();
    
    public boolean isTemporary();

    public ByteString get();

    public ByteString get(long start, int length);

    public InputStream inputStream();

    public InputStream inputStream(long start, long length);
    
    @Override
    public void close();

}
