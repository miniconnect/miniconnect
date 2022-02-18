package hu.webarticum.miniconnect.record.lob;

import java.io.InputStream;
import java.nio.charset.Charset;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class BlobValue {
    
    private final MiniContentAccess contentAccess;
    
    
    public BlobValue(MiniContentAccess contentAccess) {
        this.contentAccess = contentAccess;
    }
    
    
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }
    
    public long length() {
        return contentAccess.length();
    }

    public ByteString get(long start, int length) {
        return contentAccess.get(start, length);
    }
    
    public InputStream inputStream() {
        return contentAccess.inputStream();
    }

    public InputStream inputStream(long start, long length) {
        return contentAccess.inputStream(start, length);
    }

    public ClobValue toClob() {
        return new ClobValue(contentAccess);
    }

    public ClobValue toClob(Charset charset) {
        return new ClobValue(contentAccess, charset);
    }
    
}
