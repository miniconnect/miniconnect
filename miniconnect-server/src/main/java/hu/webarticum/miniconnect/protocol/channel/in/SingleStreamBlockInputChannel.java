package hu.webarticum.miniconnect.protocol.channel.in;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.protocol.block.Block;

public class SingleStreamBlockInputChannel implements BlockInputChannel {

    private final InputStream in;
    
    public SingleStreamBlockInputChannel(InputStream in) {
        this.in = in;
    }
    
    @Override
    public Block fetch() throws IOException {
        return new BlockReader(in).read();
    }

}
