package hu.webarticum.miniconnect.transfer.old.lab.clientserver;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.old.fetcher.CollectingConsumer;
import hu.webarticum.miniconnect.transfer.old.fetcher.DecodingBlockConsumer;

public class DemoConnector implements Closeable {
    
    private final BlockSourceFetcher fetcher;
    
    private final CollectingConsumer<DemoRequest> consumer;
    
    private final BlockTarget target; // XXX: something typed?
    

    private DemoConnector(
            DemoServer server,
            BlockSource source,
            BlockTarget target) {

        this.target = target;
        this.consumer = new CollectingConsumer<>(
                request -> server.accept(this, request));
        this.fetcher = BlockSourceFetcher.start(
                source,
                new DecodingBlockConsumer<>(
                        DemoRequest::decode,
                        consumer));
    }
    
    public static DemoConnector start(
            DemoServer server,
            BlockSource source,
            BlockTarget target) {
        
        return new DemoConnector(server, source, target);
    }

    public void send(DemoResponse response) throws IOException {
        target.send(response.encode());
    }
    
    @Override
    public void close() throws IOException {
        fetcher.close();
    }
    
}
