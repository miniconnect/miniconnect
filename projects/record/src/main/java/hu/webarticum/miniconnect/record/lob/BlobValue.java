package hu.webarticum.miniconnect.record.lob;

import java.io.InputStream;
import java.nio.charset.Charset;

import hu.webarticum.miniconnect.api.MiniContentAccess;

// TODO: functionality like JDBC BLOB
// TODO: migrate JDBC tests to here
//TODO: writeable blob?
public class BlobValue {
    
    private final MiniContentAccess contentAccess;
    
    
    public BlobValue(MiniContentAccess contentAccess) {
        this.contentAccess = contentAccess;
    }
    
    
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }
    
    public ClobValue toClob() {
        return new ClobValue(this);
    }

    public ClobValue toClob(Charset charset) {
        return new ClobValue(this, charset);
    }
    
    public InputStream inputStream() {
        return contentAccess.inputStream();
    }
    
}
