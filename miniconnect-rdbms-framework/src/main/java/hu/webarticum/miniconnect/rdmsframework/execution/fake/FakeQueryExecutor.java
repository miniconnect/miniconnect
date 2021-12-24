package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.rdmsframework.execution.DatabaseException;
import hu.webarticum.miniconnect.rdmsframework.execution.Query;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class FakeQueryExecutor implements QueryExecutor {

    @Override
    public Future<Object> execute(StorageAccess storageAccess, Query query) {
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
        
        // TODO
        //throw new DatabaseException(4, "00004", "Error four");
        throw new NumberFormatException("You are out of luck");
        
    }

}
