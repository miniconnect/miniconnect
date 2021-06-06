package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class StoredLobAccess implements MiniLobAccess {

    private final ByteString content;
    
    
    public StoredLobAccess(ByteString content) {
        this.content = content;
    }


    @Override
    public long length() {
        return content.length();
    }
    
    @Override
    public ByteString part(long start, int length) throws IOException {
        long contentLength = content.length();
        long end = start + length;
        if (start < 0L || end > contentLength) {
            throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", contentLength " + contentLength);
        }

        return content.substringLength((int) start, length);
    }

    @Override
    public InputStream inputStream() throws IOException {
        return content.asInputStream();
    }
    
    @Override
    public void close() {
        // nothing to do
    }
    
}
