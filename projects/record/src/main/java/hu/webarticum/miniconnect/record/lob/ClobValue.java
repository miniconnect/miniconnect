package hu.webarticum.miniconnect.record.lob;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;

// TODO: lazily create position index if charset is not fixed-width
// TODO: functionality like JDBC CLOB
// TODO: migrate JDBC tests to here
public class ClobValue {
    
    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    

    public ClobValue(MiniContentAccess contentAccess) {
        this(contentAccess, StandardCharsets.UTF_8);
    }
    
    public ClobValue(MiniContentAccess contentAccess, Charset charset) {
        this.contentAccess = contentAccess;
        this.charset = charset;
    }
    
    
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

    public Charset charset() {
        return charset;
    }
    
    public BlobValue toBlob() {
        return new BlobValue(contentAccess);
    }

    public Reader reader() {
        return new InputStreamReader(contentAccess.inputStream(), charset);
    }
    
}
