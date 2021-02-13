package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniSession;

public class DummyConnection implements MiniConnection {

    private volatile boolean closed = false;
    

    @Override
    public MiniSession openSession() {
        return new DummySession();
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
