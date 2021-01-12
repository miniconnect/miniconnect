package hu.webarticum.miniconnect.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.Block;

public class Server implements Runnable {
    
    private final MiniConnection connection;
    
    private final InputStream in;
    
    private final OutputStream out;
    

    public Server(MiniConnection connection, InputStream in, OutputStream out) {
        this.connection = connection;
        this.in = in;
        this.out = out;
    }


    @Override
    public void run() {
        //while (true) {
            iterate();
        //}
    }
    
    private void iterate() {
        try {
            iterateThrowing();
        } catch (IOException e) {
            
            // XXX
            e.printStackTrace();
            
        }
    }
    
    private void iterateThrowing() throws IOException {
        Block block = Block.readFrom(in);
        
        // XXX
        System.out.println("Received content: " + block.content().toString(StandardCharsets.UTF_8));
        
    }
    
}
