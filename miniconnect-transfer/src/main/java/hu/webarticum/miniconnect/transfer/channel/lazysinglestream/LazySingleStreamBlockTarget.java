package hu.webarticum.miniconnect.transfer.channel.lazysinglestream;

import java.io.IOException;
import java.io.OutputStream;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.io.BlockWriter;

public class LazySingleStreamBlockTarget implements BlockTarget {

    private OutputStreamFactory factory;
    
    private OutputStream out = null;
    
    
    public LazySingleStreamBlockTarget(OutputStreamFactory factory) {
        this.factory = factory;
    }
    
    
    @Override
    public void send(Block block) throws IOException {
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
