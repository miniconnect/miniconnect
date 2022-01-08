package hu.webarticum.miniconnect.rdmsframework.engine.impl;

import java.io.IOException;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.rdmsframework.engine.Engine;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSession;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class SimpleEngine implements Engine {
    
    private final StorageAccess storageAccess;
    
    
    public SimpleEngine(StorageAccess storageAccess) {
        this.storageAccess = storageAccess;
    }
    

    @Override
    public EngineSession openSession() {
        return new SimpleEngineSession(this);
    }
    
    public StorageAccess storageAccess() {
        return storageAccess;
    }

    @Override
    public void close() {
        if (storageAccess instanceof AutoCloseable) {
            try {
                ((AutoCloseable) storageAccess).close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (Exception e) {
                IOException ioException = new IOException("Unexpected exception");
                ioException.addSuppressed(e);
                throw new UncheckedIOException(ioException);
            }
        }
    }
    
    @Override
    public boolean isClosed() {
        return false; // FIXME
    }

}
