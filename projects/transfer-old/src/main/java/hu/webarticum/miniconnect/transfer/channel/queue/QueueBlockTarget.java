package hu.webarticum.miniconnect.transfer.channel.queue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.util.ExceptionUtil;

public class QueueBlockTarget implements BlockTarget, Closeable {
    
    private final BlockTarget baseTarget;
    
    private final BlockingQueue<Block> queue;
    
    private final Thread sendingThread;
    
    
    private volatile boolean closed = false;
    

    private QueueBlockTarget(BlockTarget baseTarget, int capacity) {
        this.baseTarget = baseTarget;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.sendingThread = new Thread(this::runSending);
    }
    
    public static QueueBlockTarget open(BlockTarget baseTarget, int capacity) {
        QueueBlockTarget wrappedTarget = new QueueBlockTarget(baseTarget, capacity);
        wrappedTarget.start();
        return wrappedTarget;
    }


    private void start() {
        sendingThread.start();
    }
    
    private void runSending() {
        while (!closed) {
            iterateSending();
            
            // FIXME
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) { }
        }
    }

    private void iterateSending() {
        try {
            iterateSendingThrowing();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            closed = true;
        } catch (InterruptedIOException e) {
            closed = true;
        } catch (Exception e) {
            
            // XXX
            e.printStackTrace();

            closed = true;
        }
    }
    
    private void iterateSendingThrowing() throws IOException, InterruptedException {
        baseTarget.send(queue.take());
    }
    
    @Override
    public void send(Block block) throws IOException {
        try {
            queue.put(block);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.combine(new InterruptedIOException(), e);
        }
    }

    @Override
    public void close() throws IOException {
        sendingThread.interrupt();
        this.closed = true;
    }

}
