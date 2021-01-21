package hu.webarticum.miniconnect.server;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.io.source.BlockSource;
import hu.webarticum.miniconnect.protocol.io.target.BlockTarget;

public class Server implements Runnable {
    
    private final MiniConnection connection;
    
    private final BlockSource source;
    
    private final BlockTarget target;
    

    public Server(
            MiniConnection connection,
            BlockSource source,
            BlockTarget target) {
        
        this.connection = connection;
        this.source = source;
        this.target = target;
    }


    @Override
    public void run() {
        while (iterate());
    }
    
    private boolean iterate() {
        try {
            iterateThrowing();
        } catch (IOException e) {
            // XXX
            return false;
        }
        return true;
    }
    
    // XXX
    private void iterateThrowing() throws IOException {
        target.send(source.fetch());
    }
    
}
