package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.io.source.BlockSource;
import hu.webarticum.miniconnect.protocol.io.target.BlockTarget;
import hu.webarticum.miniconnect.protocol.message.Request;

public class Server implements Runnable {
    
    // FIXME: connectionFactory? (each session is mapped to a MiniConnection / MiniSession)
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
    
    private void iterateThrowing() throws IOException {
        
        // XXX
        
        Block block = source.fetch();
        Request request = Request.decode(block.content());
        System.out.println(request.getClass().getName());
    }
    
}
