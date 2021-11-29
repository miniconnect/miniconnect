package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.fetcher.CollectingConsumer;
import hu.webarticum.miniconnect.transfer.fetcher.DecodingBlockConsumer;

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
