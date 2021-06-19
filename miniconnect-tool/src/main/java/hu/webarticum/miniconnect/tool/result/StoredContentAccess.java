package hu.webarticum.miniconnect.tool.result;

import java.io.InputStream;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class StoredContentAccess implements MiniContentAccess, Serializable {

    private static final long serialVersionUID = 1L;
    
    
    private final ByteString content;
    
    
    public StoredContentAccess(ByteString content) {
        this.content = content;
    }


    @Override
    public boolean isLarge() {
        return false;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public long length() {
        return content.length();
    }

    @Override
    public ByteString get() {
        return content;
    }
    
    @Override
    public ByteString get(long start, int length) {
        long contentLength = content.length();
        long end = start + length;
        if (start < 0L || end > contentLength) {
            throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", contentLength " + contentLength);
        }

        return content.substringLength((int) start, length);
    }

    @Override
    public InputStream inputStream() {
        return content.asInputStream();
    }
    
    @Override
    public void close() {
        // nothing to do
    }
    
}
