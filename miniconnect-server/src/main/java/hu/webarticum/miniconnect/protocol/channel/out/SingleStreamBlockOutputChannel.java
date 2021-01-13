package hu.webarticum.miniconnect.protocol.channel.out;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.protocol.block.Block;

public class SingleStreamBlockOutputChannel implements BlockOutputChannel {

    private final OutputStream out;
    
    public SingleStreamBlockOutputChannel(OutputStream out) {
        this.out = out;
    }
    
    @Override
    public void send(Block block) throws IOException {
        new BlockWriter(out).write(block);
    }

}
