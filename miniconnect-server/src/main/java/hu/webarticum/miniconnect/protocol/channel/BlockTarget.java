package hu.webarticum.miniconnect.protocol.channel;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;

public interface BlockTarget {

    public void send(Block block) throws IOException;
    
}
