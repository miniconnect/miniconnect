package hu.webarticum.miniconnect.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import hu.webarticum.miniconnect.api.MiniSession;

public class MiniJdbcConnection implements Connection {

    private final MiniSession miniSession;
    
    private final MiniJdbcDatabaseMetaData metaData;
    
    private volatile boolean closed = false;
    
    private final Object closeLock = new Object();
    
    private final CopyOnWriteArrayList<Statement> activeStatements = new CopyOnWriteArrayList<>();
    
    
    public MiniJdbcConnection(MiniSession session) {
        this.miniSession = session;
        this.metaData = new MiniJdbcDatabaseMetaData(this);
    }


    // --- METADATA ---
    // [start]
    
    
    public MiniSession getMiniSession() {
        return miniSession;
    }
    
    public void registerActiveStatement(Statement statement) {
        activeStatements.add(statement);
    }

    public void unregisterActiveStatement(Statement statement) {
        activeStatements.remove(statement);
    }
    
    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        if (!isWrapperFor(type)) {
            throw new SQLException(String.format("Unable to convert %s to %s", getClass(), type));
        }
        
        @SuppressWarnings("unchecked")
        T result = (T) this;
        return result;
    }

    @Override
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        return (type != null && type.isAssignableFrom(getClass()));
    }

    @Override
    public MiniJdbcDatabaseMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // [end]
    
    
    // --- CLIENT STATUS ---
    // [start]

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCatalog() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // [end]
    
    
    // --- STATEMENTS ---
    // [start]

    @Override
    public Statement createStatement() throws SQLException {
        return createStatement(
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    @Override
    public Statement createStatement(
            int resultSetType, int resultSetConcurrency) throws SQLException {
        
        return createStatement(resultSetType, resultSetConcurrency, getHoldability());
    }

    @Override
    public Statement createStatement(
            int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {

        if (resultSetType != ResultSet.TYPE_FORWARD_ONLY) {
            throw new SQLException("Only TYPE_FORWARD_ONLY result sets are supported");
        }
        if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY) {
            throw new SQLException("Only READ_ONLY result sets are supported");
        }
        if (resultSetHoldability != ResultSet.HOLD_CURSORS_OVER_COMMIT) {
            throw new SQLException("Only HOLD_CURSORS_OVER_COMMIT result sets are supported");
        }
        
        return new MiniJdbcStatement(this);
    }

    
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null; // TODO
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        
        return null; // TODO
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability
            ) throws SQLException {
        
        return null; // TODO
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int autoGeneratedKeys) throws SQLException {
        return null; // TODO
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null; // TODO
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, String[] columnNames) throws SQLException {
        
        return null; // TODO
    }

    
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null; // TODO
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        
        return null; // TODO
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        
        return null; // TODO
    }

    // [end]
    

    // --- UTILITY ---
    // [start]

    @Override
    public String nativeSQL(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // [end]
    

    // --- FACTORY ---
    // [start]

    @Override
    public Clob createClob() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // [end]
    
    
    // --- TRANSACTIONS ---
    // [start]

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void commit() throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rollback() throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    // [end]
    
    
    // --- CONNECTION STATE ---
    // [start]

    @Override
    public void close() throws SQLException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            
            for (Statement activeStatement : activeStatements) {
                activeStatement.close();
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    // [end]
    
    
}
