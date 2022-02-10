package hu.webarticum.miniconnect.impl.contentaccess.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

public class DynamicContentAccessBuilder {
    

    private DynamicContentAccessBuilder() {
        // use open() instead
    }
    
    public static DynamicContentAccessBuilder open() {
        return new DynamicContentAccessBuilder();
    }
    
    
    public DynamicContentAccessFinalBuilder writing(DynamicContentAccessBuilderWriter writer) {
        return new DynamicContentAccessFinalBuilder(writer);
    }
    
    
    public static class DynamicContentAccessFinalBuilder {
        
        private final DynamicContentAccessBuilderWriter writer;
        
        private volatile ByteString computedBytes = null; // NOSONAR
        
        
        private DynamicContentAccessFinalBuilder(DynamicContentAccessBuilderWriter writer) {
            this.writer = writer;
        }
        
        
        public MiniContentAccess build() {
            try {
                return buildThrowing();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        
        // TODO: add support for dynamically handling large data (e. g. write out to file)
        public MiniContentAccess buildThrowing() throws IOException {
            if (computedBytes == null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                writer.write(out);
                computedBytes = ByteString.wrap(out.toByteArray());
            }
            return new StoredContentAccess(computedBytes);
        }
        
    }
    
    
    @FunctionalInterface
    public interface DynamicContentAccessBuilderWriter {
        
        public void write(OutputStream out) throws IOException;
        
    }
    
}
