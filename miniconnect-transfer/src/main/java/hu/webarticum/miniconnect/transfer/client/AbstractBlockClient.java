package hu.webarticum.miniconnect.transfer.client;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;

public abstract class AbstractBlockClient implements Closeable {
    
    private final BlockSource source;
    
    private final BlockTarget target;
    
    private final Thread sourceThread;
    
    
    private volatile boolean closed = false;
    

    protected AbstractBlockClient(BlockSource source, BlockTarget target) {
        this.source = source;
        this.target = target;
        this.sourceThread = new Thread(this::acceptBlocksFromSource);
        this.sourceThread.start();
    }
    
    public void sendBlock(Block block) throws IOException {
        target.send(block);
    }

    private void acceptBlocksFromSource() {
        while (!closed) {
            Block block;
            try {
                block = source.fetch();
            } catch (Exception e) {
                
                // XXX
                //acceptException = e;
                closed = true;
                break;
                
            }
            
            try {
                acceptBlock(block);
            } catch (Exception e) {
                // TODO: what to do in this case?
            }
        }
    }
    
    protected abstract void acceptBlock(Block block);
    
    @Override
    public void close() throws IOException {
        
        // XXX
        closed = true;
        sourceThread.interrupt();
        
    }
    
}
