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
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.blob.BlobClob;
import hu.webarticum.miniconnect.jdbc.blob.WriteableBlob;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.TransactionIsolationLevel;

public class MiniJdbcConnection implements Connection {
    
    private static final Map<Integer, TransactionIsolationLevel> TRANSACTION_ISOLATION_LEVEL_MAP = new HashMap<>();
    static {
        TRANSACTION_ISOLATION_LEVEL_MAP.put(TRANSACTION_NONE, TransactionIsolationLevel.NONE);
        TRANSACTION_ISOLATION_LEVEL_MAP.put(TRANSACTION_READ_UNCOMMITTED, TransactionIsolationLevel.READ_UNCOMMITTED);
        TRANSACTION_ISOLATION_LEVEL_MAP.put(TRANSACTION_READ_COMMITTED, TransactionIsolationLevel.READ_COMMITTED);
        TRANSACTION_ISOLATION_LEVEL_MAP.put(TRANSACTION_REPEATABLE_READ, TransactionIsolationLevel.REPEATABLE_READ);
        TRANSACTION_ISOLATION_LEVEL_MAP.put(TRANSACTION_SERIALIZABLE, TransactionIsolationLevel.SERIALIZABLE);
    }
    

    private final MiniSession miniSession;
    
    private final DatabaseProvider databaseProvider;
    
    private final MiniJdbcDatabaseMetaData metaData;
    
    private final Map<String, String> clientInfo = Collections.synchronizedMap(new HashMap<>());
    
    private volatile SQLWarning currentWarningHead = null; // NOSONAR
    
    private volatile boolean closed = false;
    
    private final Object closeLock = new Object();
    
    private final CopyOnWriteArrayList<Statement> activeStatements = new CopyOnWriteArrayList<>();
    
    
    public MiniJdbcConnection(MiniSession session, DatabaseProvider databaseProvider) {
        this.miniSession = session;
        this.databaseProvider = databaseProvider;
        this.metaData = new MiniJdbcDatabaseMetaData(this);
    }


    // --- METADATA ---
    // [start]
    
    
    public MiniSession getMiniSession() {
        return miniSession;
    }

    public DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
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
        return Collections.emptyMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        if (map != null && map.size() > 0) {
            throw new SQLException("Only empty map is supported");
        }
    }
    
    void setCurrentWarningHead(SQLWarning warning) {
        currentWarningHead = warning;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return currentWarningHead;
    }

    @Override
    public void clearWarnings() throws SQLException {
        currentWarningHead = null;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        clientInfo.put(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        Map<String, String> replacements = new HashMap<>();
        properties.forEach((k, v) -> replacements.put(
                k != null ? k.toString() : null,
                v != null ? v.toString() : null));
        clientInfo.keySet().forEach(k -> replacements.putIfAbsent(k, null));
        clientInfo.putAll(replacements);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return clientInfo.get(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        Properties properties = new Properties();
        properties.putAll(clientInfo);
        return properties;
    }

    // [end]
    
    
    // --- CLIENT STATUS ---
    // [start]

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        try {
            databaseProvider.setReadOnly(miniSession, readOnly);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            return databaseProvider.isReadOnly(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        try {
            databaseProvider.setCatalog(miniSession, catalog);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        try {
            return databaseProvider.getCatalog(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        try {
            databaseProvider.setSchema(miniSession, schema);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getSchema() throws SQLException {
        try {
            return databaseProvider.getSchema(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency, getHoldability());
    }

    @Override
    public Statement createStatement(
            int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
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
        return new MiniJdbcPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        // FIXME: should we throw an exception if unsupported parameters are given?
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        // FIXME: should we throw an exception if unsupported parameters are given?
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }

    
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // [end]
    

    // --- UTILITY ---
    // [start]

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql; // TODO: SQL sequences are currently not supported
    }

    // [end]
    

    // --- FACTORY ---
    // [start]

    @Override
    public Blob createBlob() throws SQLException {
        return new WriteableBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return new BlobClob(); // TODO: use client encoding
    }

    @Override
    public NClob createNClob() throws SQLException {
        return new BlobClob(); // TODO: use client encoding
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // [end]
    
    
    // --- TRANSACTIONS ---
    // [start]

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        try {
            databaseProvider.setAutoCommit(miniSession, autoCommit);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        try {
            return databaseProvider.isAutoCommit(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void commit() throws SQLException {
        try {
            databaseProvider.commit(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void rollback() throws SQLException {
        try {
            databaseProvider.rollback(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        try {
            return new MiniJdbcSavepoint(databaseProvider.setSavepoint(miniSession));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        try {
            databaseProvider.setSavepoint(miniSession, name);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return new MiniJdbcSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            if (((MiniJdbcSavepoint) savepoint).isNamed()) {
                databaseProvider.rollbackToSavepoint(miniSession, savepoint.getSavepointName());
            } else {
                databaseProvider.rollbackToSavepoint(miniSession, savepoint.getSavepointId());
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            if (((MiniJdbcSavepoint) savepoint).isNamed()) {
                databaseProvider.releaseSavepoint(miniSession, savepoint.getSavepointName());
            } else {
                databaseProvider.releaseSavepoint(miniSession, savepoint.getSavepointId());
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        // not supported
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        try {
            databaseProvider.setTransactionIsolationLevel(
                    miniSession,
                    Objects.requireNonNull(TRANSACTION_ISOLATION_LEVEL_MAP.get(level)));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        TransactionIsolationLevel levelObject;
        try {
            levelObject = databaseProvider.getTransactionIsolationLevel(miniSession);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return TRANSACTION_ISOLATION_LEVEL_MAP.entrySet().stream() // NOSONAR: NoSuchElementException is OK
                .filter(e -> e.getValue() == levelObject)
                .map(Map.Entry::getKey)
                .findAny()
                .get();
    }

    // [end]
    
    
    // --- CONNECTION STATE ---
    // [start]

    @Override
    public void close() throws SQLException {
        synchronized (closeLock) {
            if (closed) {
                return;
            } else {
                closed = true;
            }
        }
        
        for (Statement activeStatement : activeStatements) {
            activeStatement.close();
        }
        
        try {
            miniSession.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        try {
            databaseProvider.checkSessionValid(miniSession);
        } catch (Exception e) {
            return false;
        }
        return !closed;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
       // not supported
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
       // not supported
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
    
    // [end]
    
    
}
