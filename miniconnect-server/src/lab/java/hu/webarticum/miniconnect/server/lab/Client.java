package hu.webarticum.miniconnect.server.lab;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.io.source.BlockSource;
import hu.webarticum.miniconnect.protocol.io.target.BlockTarget;
import hu.webarticum.miniconnect.protocol.message.ConnectRequest;
import hu.webarticum.miniconnect.protocol.message.Request;
import hu.webarticum.miniconnect.protocol.message.Response;
import hu.webarticum.miniconnect.protocol.message.SessionResponse;

public class Client implements Closeable {

    private final BlockSource blockSource;
    
    private final BlockTarget blockTarget;
    
    private Thread acceptThread;
    
    private Exception acceptException;
    
    
    private volatile boolean closed = false;

    
    public Client(BlockSource blockSource, BlockTarget blockTarget) {
        this.blockSource = blockSource;
        this.blockTarget = blockTarget;
        this.acceptThread = new Thread(this::acceptResponses);
        
        // XXX
        this.acceptThread.start();
    }
    
    
    public ClientSession openSession() throws IOException {
        send(new ConnectRequest());
        
        // TODO: wait for a StatusResponse
        
        return new ClientSession(this, 0);
    }

    public void send(Request request)
            throws IOException {
        
        blockTarget.send(new Block(request.encode()));
    }
    
    private void acceptResponses() {
        while (!closed) {
            Block block;
            try {
                block = blockSource.fetch();
            } catch (Exception e) {
                acceptException = e;
                closed = true;
                break;
            }
            Response response = Response.decode(block.content());
            try {
                acceptResponse(response);
            } catch (Exception e) {
                // TODO: what to do in this case?
            }
        }
    }
    
    private void acceptResponse(Response response) {
        if (response instanceof SessionResponse) {
            acceptSessionResponse((SessionResponse) response);
        } else {
            
            // XXX
            System.err.println("Non-session response: " + response.getClass().getName());
            
        }
    }

    private void acceptSessionResponse(SessionResponse sessionResponse) {
        
        // TODO
        System.out.println(String.format("Session response: %s (%d)",
                sessionResponse.getClass().getSimpleName(),
                sessionResponse.sessionId()));
        
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;
        try {
            acceptThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
