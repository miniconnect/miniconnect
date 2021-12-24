package hu.webarticum.miniconnect.jdbcadapter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;

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
        try {
            Statement jdbcStatement = jdbcConnection.createStatement(); // NOSONAR
            jdbcStatement.execute(query);
            return new JdbcAdapterResult(jdbcStatement);
        } catch (SQLException e) {
            return new JdbcAdapterResult(e);
        }
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
        
        // TODO
        
    }

}
