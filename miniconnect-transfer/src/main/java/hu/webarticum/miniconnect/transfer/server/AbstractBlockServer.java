package hu.webarticum.miniconnect.transfer.server;

import hu.webarticum.miniconnect.transfer.Block;

public abstract class AbstractBlockServer {
    

    protected AbstractBlockServer() {
        // TODO
    }
    
    
    protected abstract void acceptBlock(ClientConnector connector, Block block);
    
}
