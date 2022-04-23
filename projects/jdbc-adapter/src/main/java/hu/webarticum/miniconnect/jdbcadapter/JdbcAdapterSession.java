package hu.webarticum.miniconnect.jdbcadapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;

public class JdbcAdapterSession implements MiniSession {
    
    private final Connection jdbcConnection;
    
    private final JdbcLargeDataPutter jdbcLargeDataPutter;
    
    
    public JdbcAdapterSession(Connection jdbcConnection) {
        this(jdbcConnection, null);
    }
    
    public JdbcAdapterSession(Connection jdbcConnection, JdbcLargeDataPutter jdbcLargeDataPutter) {
        this.jdbcConnection = jdbcConnection;
        this.jdbcLargeDataPutter = jdbcLargeDataPutter;
    }
    

    @Override
    public MiniResult execute(String query) {
        Statement jdbcStatement;
        try {
            jdbcStatement = jdbcConnection.createStatement(); // NOSONAR
            jdbcStatement.execute(query);
        } catch (SQLException e) {
            return new JdbcAdapterResult(e);
        } catch (Exception e) {
            return errorResult(e);
        }
        
        try {
            return new JdbcAdapterResult(jdbcStatement);
        } catch (Exception e) {
            return errorResult(e);
        }
    }
    
    private MiniResult errorResult(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            message = "Unexpected " + e.getClass().getName();
        }
        return new StoredResult(new StoredError(1, "00001", message));
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        
        if (jdbcLargeDataPutter == null) {
            throw new UnsupportedOperationException();
        }
        
        return jdbcLargeDataPutter.putLargeData(jdbcConnection, variableName, length, dataSource);
    }

    @Override
    public void close() {
        try {
            jdbcConnection.close();
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException(e));
        }
    }

}
