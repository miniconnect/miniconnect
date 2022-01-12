package hu.webarticum.miniconnect.transfer.old.lab.clientserver;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

public class DemoServer implements Closeable {
    
    private final UnaryOperator<String> transformer;
    
    private final ExecutorService executorService;
    
    private final boolean isExecutorServiceOwned;
    

    private DemoServer(
            UnaryOperator<String> transformer,
            ExecutorService executorService,
            boolean isExecutorServiceOwned) {
        
        this.transformer = transformer;
        this.executorService = executorService;
        this.isExecutorServiceOwned = isExecutorServiceOwned;
    }

    public static DemoServer start(UnaryOperator<String> transformer) {
        return new DemoServer(transformer, Executors.newCachedThreadPool(), true);
    }
    
    public static DemoServer start(
            UnaryOperator<String> transformer,
            ExecutorService executorService) {
        
        return new DemoServer(transformer, executorService, false);
    }
    

    public void accept(DemoConnector connector, DemoRequest request) {
        executorService.submit(() -> this.process(connector, request));
    }
    
    public Object process(DemoConnector connector, DemoRequest request) throws IOException {
        int exchangeId = request.exchangeId();
        String query = request.query();
        String answer = transformer.apply(query);
        DemoResponse response = new DemoResponse(exchangeId, answer);
        connector.send(response);
        return null;
    }
    
    @Override
    public void close() throws IOException {
        if (isExecutorServiceOwned) {
            executorService.shutdown();
        }
    }
    
}
