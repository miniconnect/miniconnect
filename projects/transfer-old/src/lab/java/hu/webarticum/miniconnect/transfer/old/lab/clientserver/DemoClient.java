package hu.webarticum.miniconnect.transfer.old.lab.clientserver;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.old.fetcher.CollectingConsumer;
import hu.webarticum.miniconnect.transfer.old.fetcher.CollectingNotifier;
import hu.webarticum.miniconnect.transfer.old.fetcher.DecodingBlockConsumer;
import hu.webarticum.miniconnect.transfer.old.fetcher.pocket.SingleItemPocket;

public class DemoClient implements Closeable {
    
    private final BlockSourceFetcher fetcher;
    
    private final CollectingConsumer<DemoResponse> consumer;
    
    private final BlockTarget target; // XXX: something typed?
    
    private final AtomicInteger exchangeIdCounter = new AtomicInteger(1);
    

    private DemoClient(
            BlockSourceFetcher fetcher,
            CollectingConsumer<DemoResponse> consumer,
            BlockTarget target) {

        this.fetcher = fetcher;
        this.consumer = consumer;
        this.target = target;
    }
    
    public static DemoClient start(BlockSource source, BlockTarget target) {
        CollectingConsumer<DemoResponse> consumer = new CollectingConsumer<>(
                response -> System.out.println(String.format("FALLBACK: %s", response.result())));
        BlockSourceFetcher fetcher = BlockSourceFetcher.start(
                source,
                new DecodingBlockConsumer<>(
                        DemoResponse::decode,
                        consumer));
        return new DemoClient(fetcher, consumer, target);
    }


    public String query(String query) throws IOException {
        int exchangeId = exchangeIdCounter.getAndIncrement();
        
        SingleItemPocket<DemoResponse> pocket = new SingleItemPocket<>(
                response -> response.exchangeId() == exchangeId);
        CollectingNotifier<DemoResponse, DemoResponse> notifier = consumer.listen(pocket);

        DemoRequest request = new DemoRequest(exchangeId, query);
        target.send(request.encode());
        DemoResponse response = notifier.await();
        
        return response.result();
    }
    
    @Override
    public void close() throws IOException {
        fetcher.close();
    }
    
}
