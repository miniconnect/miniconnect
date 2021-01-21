package hu.webarticum.miniconnect.protocol.io.source;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;

public interface BlockSource {

    public Block fetch() throws IOException;
    
}
