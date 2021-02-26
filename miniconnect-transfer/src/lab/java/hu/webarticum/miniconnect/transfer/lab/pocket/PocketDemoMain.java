package hu.webarticum.miniconnect.transfer.lab.pocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.transfer.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.fetcher.CollectingConsumer;
import hu.webarticum.miniconnect.transfer.fetcher.CollectingNotifier;
import hu.webarticum.miniconnect.transfer.fetcher.DecodingBlockConsumer;
import hu.webarticum.miniconnect.transfer.fetcher.pocket.SingleItemPocket;
import hu.webarticum.miniconnect.transfer.util.ByteString;

public class PocketDemoMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);

        BlockTarget target = new SingleStreamBlockTarget(out);
        BlockSource source = new SingleStreamBlockSource(in);
        
        CollectingConsumer<String> collectingConsumer = new CollectingConsumer<>(
                item -> System.out.println(String.format("FALLBACK: %s", item)));
        
        try (BlockSourceFetcher fetcher = createFetcher(source, collectingConsumer)) {
            SingleItemPocket<String> pocket = new SingleItemPocket<>(item -> item.equals("bbb"));
            CollectingNotifier<String> notifier = collectingConsumer.listen(pocket);
            Thread thread = sendMessagesOnOtherThread(target);
            
            synchronized (notifier) {
                while (!pocket.isCompleted()) {
                    notifier.wait();
                }
            }
            
            System.out.println(String.format("FOUND: %s", pocket.get()));
            
            thread.join();
        }
    }
    
    private static Thread sendMessagesOnOtherThread(BlockTarget target) {
        Thread thread = new Thread(() -> sendMessages(target));
        thread.start();
        return thread;
    }

    private static void sendMessages(BlockTarget target) {
        try {
            target.send(blockOf("aaa"));
            target.send(blockOf("bbb"));
            target.send(blockOf("ccc"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static BlockSourceFetcher createFetcher(
            BlockSource source, CollectingConsumer<String> collectingConsumer) {

        return BlockSourceFetcher.start(
                source,
                new DecodingBlockConsumer<>(
                        PocketDemoMain::stringOf,
                        collectingConsumer));
    }

    public static String stringOf(Block block) {
        return block.content().toString(StandardCharsets.UTF_8);
    }

    public static Block blockOf(String string) {
        ByteString content = ByteString.wrap(string.getBytes(StandardCharsets.UTF_8));
        return new Block(content);
    }

}
