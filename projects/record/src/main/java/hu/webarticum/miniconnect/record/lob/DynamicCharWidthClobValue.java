package hu.webarticum.miniconnect.record.lob;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class DynamicCharWidthClobValue implements ClobValue {

    public static final int DYNAMIC_CHAR_WIDTH = -1;
    

    private static final long UNSPECIFIED_LENGTH = -1;

    
    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    
    private volatile long cachedLength = UNSPECIFIED_LENGTH;
    

    public DynamicCharWidthClobValue(MiniContentAccess contentAccess, Charset charset) {
        this.contentAccess = contentAccess;
        this.charset = charset;
    }

    
    @Override
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    @Override
    public long length() {
        if (cachedLength == UNSPECIFIED_LENGTH) {
            cachedLength = calculateLength();
        }
        
        return cachedLength;
    }

    private long calculateLength() {
        
        // TODO
        return 0L;
        
    }

    @Override
    public String get(long start, int length) {
        
        // TODO
        return null;
        
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(contentAccess.inputStream(), charset);
    }

    @Override
    public Reader reader(long start, long length) {
        // TODO
        return null;
        
    }

}
