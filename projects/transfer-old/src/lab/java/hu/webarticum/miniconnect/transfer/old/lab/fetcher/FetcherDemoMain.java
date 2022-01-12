package hu.webarticum.miniconnect.transfer.old.lab.fetcher;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.transfer.old.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.old.lab.util.BlockUtil;

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
                        BlockUtil.stringOf(block))))) {
            for (int i = 0; i < 5; i++) {
                target.send(BlockUtil.dataBlockOf(String.format("ITEM(%d)", i)));
                Thread.sleep(500);
            }
        }
        
        System.out.println("Finished.");
    }
    
}
