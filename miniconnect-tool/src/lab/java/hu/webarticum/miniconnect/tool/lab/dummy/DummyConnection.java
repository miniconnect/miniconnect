package hu.webarticum.miniconnect.tool.lab.dummy;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniSession;

public class DummyConnection implements MiniConnection {

    private volatile boolean closed = false;


    @Override
    public MiniSession openSession() {
        if (closed) {
            throw new IllegalStateException("Already closed");
        }

        return new DummySession();
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

}
