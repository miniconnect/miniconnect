package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.DatabaseException;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSession;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class FrameworkSession implements MiniSession, CheckableCloseable {
    
    private final EngineSession engineSession;
    
    
    public FrameworkSession(EngineSession engineSession) {
        this.engineSession = engineSession;
    }
    
    
    @Override
    public MiniResult execute(String sql) {
        checkClosed();
        try {
            SqlParser sqlParser = engineSession.sqlParser();
            Query query = sqlParser.parse(sql);
            return execute(query);
        } catch (Exception e) {
            return resultOfException(e);
        }
    }
    
    public MiniResult execute(Query query) {
        checkClosed();
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
        return resultOfException(exception);
    }

    public MiniResult executeThrowing(Query query) throws InterruptedException, ExecutionException {
        QueryExecutor queryExecutor = engineSession.queryExecutor();
        StorageAccess storageAccess = engineSession.storageAccess();
        Future<Object> future = queryExecutor.execute(storageAccess, query); // TODO
        Object executionResult = future.get(); // TODO
        
        // TODO
        
        return new StoredResult(new StoredError(99, "00099", "No error occured"));
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        checkClosed();
        
        // TODO
        return null;
        
    }

    @Override
    public void close() {
        engineSession.close();
    }

    @Override
    public boolean isClosed() {
        return engineSession.isClosed();
    }
    
    private MiniResult resultOfException(Throwable exception) {
        if (!(exception instanceof DatabaseException)) {
            String message = "Unexpected error: " + extractMessage(exception);
            return new StoredResult(new StoredError(99999, "99999", message));
        }
        
        DatabaseException databaseException = (DatabaseException) exception;
        return new StoredResult(new StoredError(
                databaseException.code(),
                databaseException.sqlState(),
                databaseException.message()));
    }
    
    private String extractMessage(Throwable exception) {
        String message = exception.getMessage();
        if (message != null) {
            return message;
        }
        
        Throwable cause = exception.getCause();
        if (cause != null) {
            return extractMessage(cause);
        }
        
        return exception.getClass().getName();
    }

}
