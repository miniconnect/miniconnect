package hu.webarticum.miniconnect.transfer.old.fetcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;

public class BlockSourceFetcher implements Closeable {
    
    private final BlockSource source;
    
    private final Consumer<Block> consumer;
    
    private final Thread fetcherThread;
    
    
    private BlockSourceFetcher(BlockSource source, Consumer<Block> consumer) {
        this.source = source;
        this.consumer = consumer;
        this.fetcherThread = new Thread(this::runFetch);
    }
    

    public static BlockSourceFetcher start(BlockSource source, Consumer<Block> consumer) {
        BlockSourceFetcher fetcher = new BlockSourceFetcher(source, consumer);
        fetcher.fetcherThread.start();
        return fetcher;
    }
    
    // XXX
    private void runFetch() {
        while (true) {
            Block block;
            try {
                block = source.fetch();
            } catch (IOException e) {
                
                // TODO what to do?
                return;
                
            }
            consumer.accept(block);
        }
    }
    
    @Override
    public void close() throws IOException {
        fetcherThread.interrupt();
        try {
            fetcherThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
}
