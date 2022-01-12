package hu.webarticum.miniconnect.transfer.old.channel;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.old.Block;

public interface BlockTarget {

    public void send(Block block) throws IOException;
    
}
