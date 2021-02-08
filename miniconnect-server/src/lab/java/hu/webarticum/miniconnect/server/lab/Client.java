package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.io.source.BlockSource;
import hu.webarticum.miniconnect.protocol.io.target.BlockTarget;
import hu.webarticum.miniconnect.protocol.message.ConnectRequest;
import hu.webarticum.miniconnect.protocol.message.Request;

// TODO: close...
public class Client {

    private final BlockSource blockSource;
    
    private final BlockTarget blockTarget;

    
    public Client(BlockSource blockSource, BlockTarget blockTarget) {
        this.blockSource = blockSource;
        this.blockTarget = blockTarget;
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

}
