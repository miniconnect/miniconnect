package hu.webarticum.miniconnect.protocol.io.source;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.protocol.block.Block;

public class SingleStreamBlockSource implements BlockSource {

    private final InputStream in;
    
    public SingleStreamBlockSource(InputStream in) {
        this.in = in;
    }
    
    @Override
    public Block fetch() throws IOException {
        return new BlockReader(in).read();
    }

}
