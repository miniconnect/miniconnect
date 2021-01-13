package hu.webarticum.miniconnect.protocol.channel.out;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;

public interface BlockOutputChannel {

    public void send(Block block) throws IOException;
    
}
