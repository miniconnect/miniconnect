package hu.webarticum.miniconnect.transfer.channel;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.Block;

public interface BlockSource {

    public Block fetch() throws IOException;
    
}
