package hu.webarticum.miniconnect.transfer.old.channel.singlestream;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.io.BlockWriter;

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
