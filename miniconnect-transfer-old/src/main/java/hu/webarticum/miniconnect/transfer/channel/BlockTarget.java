package hu.webarticum.miniconnect.transfer.channel;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.Block;

public interface BlockTarget {

    public void send(Block block) throws IOException;
    
}
