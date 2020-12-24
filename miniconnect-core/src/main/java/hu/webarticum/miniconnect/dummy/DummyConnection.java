package hu.webarticum.miniconnect.dummy;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniResult;

public class DummyConnection implements MiniConnection {

    private volatile boolean closed = false;
    
    
    @Override
    public MiniResult execute(String query) {
        if (closed) {
            throw new IllegalStateException("Already closed");
        }
        
        // TODO
        return null;
        
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

}
