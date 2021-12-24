package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.rdmsframework.execution.ParsingSqlExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeSqlParser;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResult;

public class FakeFrameworkSession implements MiniSession {
    
    @Override
    public MiniResult execute(String query) {
        SqlExecutor sqlExecutor =
                new ParsingSqlExecutor(new FakeSqlParser(), new FakeQueryExecutor());
        Future<Object> future = sqlExecutor.execute(query); // TODO
        Exception exception = null;
        Object executionResult = null;
        try {
            executionResult = future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exception = e;
        } catch (ExecutionException e) {
            exception = (Exception) e.getCause();
        } catch (Exception e) {
            exception = e;
        }
        MiniResult result;
        if (exception != null) {
            result = new StoredResult(new StoredError(1, "00001", exception.getMessage()));
        } else {
            result = new StoredResult(new StoredError(99, "00099", "Nincs hiba sajnos..."));
        }
        return result;
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
