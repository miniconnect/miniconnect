package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.rdmsframework.DatabaseException;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResult;

public class FrameworkSession implements MiniSession {
    
    private final Supplier<SqlParser> sqlParserFactory;
    
    private final Supplier<QueryExecutor> queryExecutorFactory;
    
    private final StorageAccess storageAccess;
    
    
    private volatile boolean closed = false;
    
    
    // TODO: create an abstraction to storageAccess (transaction manager)
    public FrameworkSession(
            Supplier<SqlParser> sqlParserFactory,
            Supplier<QueryExecutor> queryExecutorFactory,
            Supplier<StorageAccess> storageAccessFactory) {
        this.sqlParserFactory = sqlParserFactory;
        this.queryExecutorFactory = queryExecutorFactory;
        this.storageAccess = storageAccessFactory.get();
    }
    
    
    @Override
    public MiniResult execute(String sql) {
        checkClosed();
        try {
            SqlParser sqlParser = sqlParserFactory.get();
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
        QueryExecutor queryExecutor = queryExecutorFactory.get();
        Future<Object> future = queryExecutor.execute(storageAccess, query); // TODO
        Object executionResult = future.get(); // TODO
        
        // TODO
        
        return new StoredResult(new StoredError(99, "00099", "No error"));
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        if (closed) {
            throw new IllegalStateException("Session is closed");
        }
        
        // TODO
        return null;
        
    }

    @Override
    public void close() {
        closed = true;
        if (storageAccess instanceof AutoCloseable) {
            try {
                ((AutoCloseable) storageAccess).close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (Exception e) {
                IOException ioException = new IOException("Unexpected exception");
                ioException.addSuppressed(e);
                throw new UncheckedIOException(ioException);
            }
        }
    }
    
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Session is closed");
        }
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
