package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.rdmsframework.DatabaseException;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class FakeQueryExecutor implements QueryExecutor {

    @Override
    public Future<Object> execute(StorageAccess storageAccess, Query query) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        try {
            Object result = createResult(query);
            future.complete(result);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
    
    public Object createResult(Query query) {

        // TODO
        throw new DatabaseException(99, "00099", "SQL: " + query);
        
    }

}
