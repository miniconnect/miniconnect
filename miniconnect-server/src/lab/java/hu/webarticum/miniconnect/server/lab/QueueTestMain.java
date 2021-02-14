package hu.webarticum.miniconnect.server.lab;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.BlockSource;
import hu.webarticum.miniconnect.protocol.channel.BlockTarget;
import hu.webarticum.miniconnect.protocol.channel.queue.QueueBlockSource;
import hu.webarticum.miniconnect.protocol.channel.queue.QueueBlockTarget;
import hu.webarticum.miniconnect.protocol.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.protocol.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.protocol.common.ByteString;

public class QueueTestMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream innerOut = new PipedOutputStream(); // XXX
        InputStream innerIn = new PipedInputStream(innerOut);

        BlockTarget innerBlockTarget = new SingleStreamBlockTarget(innerOut);
        BlockSource innerBlockSource = new SingleStreamBlockSource(innerIn);
        
        try (QueueBlockTarget queueBlockTarget = QueueBlockTarget.open(innerBlockTarget, 2)) {
            try (QueueBlockSource queueBlockSource = QueueBlockSource.open(innerBlockSource, 2)) {
                queueBlockTarget.send(new Block(ByteString.wrap("alma körte".getBytes(StandardCharsets.UTF_8))));
                queueBlockTarget.send(new Block(ByteString.wrap("xxx yyy".getBytes(StandardCharsets.UTF_8))));
                queueBlockTarget.send(new Block(ByteString.wrap("lorem ipsum".getBytes(StandardCharsets.UTF_8))));
    
                Thread.sleep(1000);
                
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
                System.out.println(queueBlockSource.fetch().content().toString(StandardCharsets.UTF_8));
            }
        }
    }
    
}
