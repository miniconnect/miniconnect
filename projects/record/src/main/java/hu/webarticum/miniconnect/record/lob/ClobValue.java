package hu.webarticum.miniconnect.record.lob;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// TODO: lazily create position index if charset is not fixed-width
// TODO: functionality like JDBC CLOB
// TODO: migrate JDBC tests to here
// TODO: writeable clob?
public class ClobValue {
    
    private final BlobValue blob;
    
    private final Charset charset;
    

    public ClobValue(BlobValue blob) {
        this(blob, StandardCharsets.UTF_8);
    }
    
    public ClobValue(BlobValue blob, Charset charset) {
        this.blob = blob;
        this.charset = charset;
    }
    
    
    public BlobValue blob() {
        return blob;
    }

    public Charset charset() {
        return charset;
    }
    
    public Reader reader() {
        return new InputStreamReader(blob.inputStream(), charset);
    }
    
}
