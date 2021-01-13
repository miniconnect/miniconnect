package hu.webarticum.miniconnect.protocol.channel.in;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;

public interface BlockInputChannel {

    public Block fetch() throws IOException;
    
}
