package hu.webarticum.miniconnect.transfer.old.channel;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.old.Block;

public interface BlockSource {

    public Block fetch() throws IOException;
    
}
