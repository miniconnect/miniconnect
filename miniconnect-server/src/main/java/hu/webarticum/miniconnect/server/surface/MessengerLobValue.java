package hu.webarticum.miniconnect.server.surface;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.server.lob.FileAsynchronousLobAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerLobValue implements MiniValue, Closeable {
    
    private final MiniValueDefinition definition;
    
    private final FileAsynchronousLobAccess lobAccess;
    
    private final ByteString initialContent;
    
    private final Object closeLock = new Object();
    
    
    private boolean wasLobAccessRequested;
    
    private boolean closed;
    

    public MessengerLobValue(
            MiniValueDefinition definition,
            FileAsynchronousLobAccess lobAccess,
            ByteString initialContent) {
        
        this.definition = definition;
        this.lobAccess = lobAccess;
        this.initialContent = initialContent;
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
    public boolean isLob() {
        return true;
    }

    @Override
    public long length() {
        return lobAccess.length();
    }

    @Override
    public ByteString content() {
        return initialContent;
    }

    @Override
    public MiniLobAccess lobAccess() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                throw new IOException("LOB was already closed");
            }
            wasLobAccessRequested = true;
        }
        
        return lobAccess;
    }
    
    @Override
    public void close() throws IOException {
        boolean shouldCloseLobAccess;
        
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            
            shouldCloseLobAccess = !wasLobAccessRequested;
            closed = true;
        }
        
        if (shouldCloseLobAccess) {
            lobAccess.close();
        }
    }

}
