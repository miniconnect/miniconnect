package hu.webarticum.miniconnect.transfer.lab.fetcher;

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
import hu.webarticum.miniconnect.transfer.util.ByteString;

public class FetcherDemoMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);

        BlockTarget target = new SingleStreamBlockTarget(out);
        BlockSource source = new SingleStreamBlockSource(in);
        
        try (BlockSourceFetcher fetcher = BlockSourceFetcher.start(
                source,
                block -> System.out.println(String.format(
                        "Incoming block: %s",
                        stringOf(block))))) {
            for (int i = 0; i < 5; i++) {
                target.send(blockOf(String.format("ITEM(%d)", i)));
                Thread.sleep(500);
            }
        }
        
        System.out.println("Finished.");
    }
    
    public static String stringOf(Block block) {
        return block.content().toString(StandardCharsets.UTF_8);
    }

    public static Block blockOf(String string) {
        ByteString content = ByteString.wrap(string.getBytes(StandardCharsets.UTF_8));
        return new Block(content);
    }

}
