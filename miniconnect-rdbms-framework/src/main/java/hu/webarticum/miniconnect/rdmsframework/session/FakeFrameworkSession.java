package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.rdmsframework.execution.Query;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeSqlParser;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResult;

public class FakeFrameworkSession implements MiniSession {
    
    @Override
    public MiniResult execute(String sql) {
        Query query = new FakeSqlParser().parse(sql);
        return execute(query);
    }
    
    public MiniResult execute(Query query) {
        Exception exception;
        try {
            return executeThrowing(query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exception = e;
        } catch (ExecutionException e) {
            exception = (Exception) e.getCause();
        } catch (Exception e) {
            exception = e;
        }
        return new StoredResult(new StoredError(1, "00001", exception.getMessage()));
    }

    public MiniResult executeThrowing(Query query) throws InterruptedException, ExecutionException {
        QueryExecutor queryExecutor = new FakeQueryExecutor();
        Future<Object> future = queryExecutor.execute(query); // TODO
        Object executionResult = future.get(); // TODO
        return new StoredResult(new StoredError(99, "00099", "Nincs hiba sajnos..."));
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        
        // TODO
        return null;
        
    }

    @Override
    public void close() throws IOException {
        
        // TODO
        
    }

}
