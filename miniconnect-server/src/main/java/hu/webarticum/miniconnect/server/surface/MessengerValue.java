package hu.webarticum.miniconnect.server.surface;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.server.contentaccess.FileChargeableContentAccess;

public class MessengerValue implements MiniValue, Closeable {
    
    private final MiniValueDefinition definition;
    
    // FIXME
    private final FileChargeableContentAccess contentAccess;
    
    private final Object closeLock = new Object();
    
    
    private boolean wasContentAccessRequested;
    
    private boolean closed;
    

    public MessengerValue(
            MiniValueDefinition definition, FileChargeableContentAccess contentAccess) {
        
        this.definition = definition;
        this.contentAccess = contentAccess;
    }
    
    
    @Override
    public MiniValueDefinition definition() {
        return definition;
    }
    
    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public MiniContentAccess contentAccess() {
        synchronized (closeLock) {
            if (closed) {
                throw new IllegalStateException("Content access was already closed");
            }
            wasContentAccessRequested = true;
        }
        
        return contentAccess;
    }
    
    @Override
    public void close() throws IOException {
        boolean shouldCloseContentAccess;
        
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            
            shouldCloseContentAccess = !wasContentAccessRequested;
            closed = true;
        }
        
        if (shouldCloseContentAccess) {
            contentAccess.close();
        }
    }

}
