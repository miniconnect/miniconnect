package hu.webarticum.miniconnect.transfer.server;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerScheduler<Q, R> implements Server<Q, R>, Closeable {
    
    private final Server<Q, R> server;

    private final ExecutorService executorService;
    
    private final boolean isExecutorServiceOwned;
    

    private ServerScheduler(
            Server<Q, R> server,
            ExecutorService executorService,
            boolean isExecutorServiceOwned) {
        
        this.server = server;
        this.executorService = executorService;
        this.isExecutorServiceOwned = isExecutorServiceOwned;
    }

    public static <Q, R> ServerScheduler<Q, R> start(Server<Q, R> server) {
        return new ServerScheduler<>(server, Executors.newCachedThreadPool(), true);
    }
    
    public static <Q, R> ServerScheduler<Q, R> start(
            Server<Q, R> server,
            ExecutorService executorService) {
        
        return new ServerScheduler<>(server, executorService, false);
    }
    
    
    @Override
    public void accept(Q request, ResponseTarget<R> responseTarget) {
        executorService.submit(() -> server.accept(request, responseTarget));
    }

    @Override
    public void close() throws IOException {
        if (isExecutorServiceOwned) {
            executorService.shutdown();
        }
    }
    
}
