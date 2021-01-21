package hu.webarticum.miniconnect.protocol.io.target;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.protocol.block.Block;

public class SingleStreamBlockTarget implements BlockTarget {

    private final OutputStream out;
    
    public SingleStreamBlockTarget(OutputStream out) {
        this.out = out;
    }
    
    @Override
    public void send(Block block) throws IOException {
        new BlockWriter(out).write(block);
    }

}
