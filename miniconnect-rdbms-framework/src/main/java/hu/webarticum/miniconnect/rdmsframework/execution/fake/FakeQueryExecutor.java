package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.rdmsframework.execution.Query;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;

public class FakeQueryExecutor implements QueryExecutor {

    @Override
    public Future<Object> execute(Query query) {
        if (!(query instanceof FakeQuery)) {
            throw new IllegalArgumentException("Only fake query could be accepted");
        }
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        try {
            Object result = createResult();
            future.complete(result);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
    
    public Object createResult() {
        
        throw new NumberFormatException("you are out of luck"); // TODO
        
    }

}
