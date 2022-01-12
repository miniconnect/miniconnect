package hu.webarticum.miniconnect.transfer.old.lab.queue;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.channel.queue.QueueBlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.queue.QueueBlockTarget;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.util.data.ByteString;

public class QueueTestMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);

        BlockTarget target = new SingleStreamBlockTarget(out);
        BlockSource source = new SingleStreamBlockSource(in);
        
        try (QueueBlockTarget queueTarget = QueueBlockTarget.open(target, 2)) {
            try (QueueBlockSource queueSource = QueueBlockSource.open(source, 2)) {
                queueTarget.send(Block.dataOf(ByteString.wrap("alma körte".getBytes(StandardCharsets.UTF_8))));
                queueTarget.send(Block.dataOf(ByteString.wrap("xxx yyy".getBytes(StandardCharsets.UTF_8))));
                queueTarget.send(Block.dataOf(ByteString.wrap("lorem ipsum".getBytes(StandardCharsets.UTF_8))));

                System.out.println("Sent.");
                System.out.println("Sleep 1 second before fetch...");
                Thread.sleep(1000);
                
                System.out.println(queueSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueSource.fetch().content().toString(StandardCharsets.UTF_8));
            }
        }
    }
    
}
