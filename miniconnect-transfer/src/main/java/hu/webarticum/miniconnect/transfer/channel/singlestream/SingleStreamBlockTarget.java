package hu.webarticum.miniconnect.transfer.channel.singlestream;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.io.BlockWriter;

public class SingleStreamBlockTarget implements BlockTarget {

    private final OutputStream out;
    
    public SingleStreamBlockTarget(OutputStream out) {
        this.out = out;
    }
    
    @Override
    public synchronized void send(Block block) throws IOException {
        new BlockWriter(out).write(block);
    }

}
