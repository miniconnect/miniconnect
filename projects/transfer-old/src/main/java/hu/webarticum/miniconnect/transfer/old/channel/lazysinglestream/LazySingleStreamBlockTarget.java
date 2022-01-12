package hu.webarticum.miniconnect.transfer.old.channel.lazysinglestream;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.io.BlockWriter;

public class LazySingleStreamBlockTarget implements BlockTarget {

    private OutputStreamFactory factory;
    
    private OutputStream out = null;
    
    
    public LazySingleStreamBlockTarget(OutputStreamFactory factory) {
        this.factory = factory;
    }
    
    
    @Override
    public synchronized void send(Block block) throws IOException {
        new BlockWriter(requireOutputStream()).write(block);
    }

    private OutputStream requireOutputStream() throws IOException {
        if (out == null) {
            out = factory.open();
            factory = null;
        }
        
        return out;
    }

}
