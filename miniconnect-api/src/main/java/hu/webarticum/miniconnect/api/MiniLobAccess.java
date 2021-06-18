package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.util.data.ByteString;

public interface MiniLobAccess extends Closeable {
    
    public long length();
    
    // TODO: public boolean isLarge();
    
    // TODO: public boolean isInMemory();

    public ByteString get() throws IOException;

    public ByteString get(long start, int length) throws IOException;

    public InputStream inputStream() throws IOException;

}
