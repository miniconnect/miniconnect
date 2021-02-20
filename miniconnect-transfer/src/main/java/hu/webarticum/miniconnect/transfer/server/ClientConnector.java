package hu.webarticum.miniconnect.transfer.server;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.client.AbstractBlockClient;

public class ClientConnector extends AbstractBlockClient {
    
    private final AbstractBlockServer server;
    

    public ClientConnector(AbstractBlockServer server, BlockSource source, BlockTarget target) {
        super(source, target);
        this.server = server;
    }

    
    @Override
    protected void acceptBlock(Block block) {
        server.acceptBlock(this, block);
    }

}
