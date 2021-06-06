package hu.webarticum.miniconnect.server.surface;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.server.lob.AsynchronousLobAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerLobValue implements MiniValue, Closeable {
    
    private final AsynchronousLobAccess lobAccess;
    
    private final ByteString initialContent;
    
    private final Object closeLock = new Object();
    
    
    private boolean wasLobAccessRequested;
    
    private boolean closed;
    

    public MessengerLobValue(AsynchronousLobAccess lobAccess, ByteString initialContent) {
        this.lobAccess = lobAccess;
        this.initialContent = initialContent;
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
