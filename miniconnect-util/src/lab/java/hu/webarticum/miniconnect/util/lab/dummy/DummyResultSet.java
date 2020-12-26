package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniResultSet;

public class DummyResultSet implements MiniResultSet {

    private volatile boolean closed = false;
    
    
    // TODO
    
    
    @Override
    public void close() throws IOException {
        closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

}
