package hu.webarticum.miniconnect.transfer.old.channel.singlestream;

import java.io.IOException;
import java.io.InputStream;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.io.BlockReader;

public class SingleStreamBlockSource implements BlockSource {

    private final InputStream in;
    
    public SingleStreamBlockSource(InputStream in) {
        this.in = in;
    }
    
    @Override
    public synchronized Block fetch() throws IOException {
        return new BlockReader(in).read();
    }

}
