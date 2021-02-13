package hu.webarticum.miniconnect.protocol.channel.singlestream;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.BlockSource;
import hu.webarticum.miniconnect.protocol.io.BlockReader;

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
