package hu.webarticum.miniconnect.transfer.channel.lazysinglestream;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.io.BlockReader;

public class LazySingleStreamBlockSource implements BlockSource {

    private InputStreamFactory factory;

    private InputStream in = null;
    
    
    public LazySingleStreamBlockSource(InputStreamFactory factory) {
        this.factory = factory;
    }
    
    
    @Override
    public synchronized Block fetch() throws IOException {
        return new BlockReader(requireInputStream()).read();
    }
    
    private InputStream requireInputStream() throws IOException {
        if (in == null) {
            in = factory.open();
            factory = null;
        }
        
        return in;
    }

}
