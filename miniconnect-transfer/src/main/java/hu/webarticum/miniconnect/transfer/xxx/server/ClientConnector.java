package hu.webarticum.miniconnect.transfer.xxx.server;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.xxx.client.AbstractBlockClient;

public class ClientConnector extends AbstractBlockClient {
    
    private final AbstractBlockServer server;
    

    public ClientConnector(AbstractBlockServer server, BlockSource source, BlockTarget target) {
        super(source, target);
        this.server = server;
    }

    
    @Override
    protected void acceptBlockInternal(Block block) {
        server.acceptBlock(this, block);
    }
    
    public void sendBlock(Block block) throws IOException {
        sendBlockInternal(block);
    }

}
