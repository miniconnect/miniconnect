package hu.webarticum.miniconnect.transfer.channel.queue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.util.ExceptionUtil;

public class QueueBlockSource implements BlockSource, Closeable {
    
    private final BlockSource baseSource;
    
    private final BlockingQueue<Block> queue;
    
    private final Thread fetchingThread;
    
    
    private volatile boolean closed = false;
    

    private QueueBlockSource(BlockSource baseSource, int capacity) {
        this.baseSource = baseSource;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.fetchingThread = new Thread(this::runFetching);
    }
    
    public static QueueBlockSource open(BlockSource baseSource, int capacity) {
        QueueBlockSource wrappedTarget = new QueueBlockSource(baseSource, capacity);
        wrappedTarget.start();
        return wrappedTarget;
    }
    
    
    private void start() {
        fetchingThread.start();
    }
    
    private void runFetching() {
        while (!closed) {
            iterateFetching();
        }
    }

    private void iterateFetching() {
        try {
            iterateFetchingThrowing();
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
    
    private void iterateFetchingThrowing() throws IOException, InterruptedException {
        queue.put(baseSource.fetch());
    }

    @Override
    public Block fetch() throws IOException {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.combine(new InterruptedIOException(), e);
        }
    }

    @Override
    public void close() throws IOException {
        fetchingThread.interrupt();
        this.closed = true;
    }

}
