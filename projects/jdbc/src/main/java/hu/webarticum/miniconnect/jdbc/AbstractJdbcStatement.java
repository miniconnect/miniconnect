package hu.webarticum.miniconnect.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import hu.webarticum.miniconnect.api.MiniResult;

public abstract class AbstractJdbcStatement implements Statement {
    
    private final MiniJdbcConnection connection;
    
    private volatile boolean escapeProcessing = false;
    
    private volatile ResultHolder currentResult = null; // NOSONAR

    
    AbstractJdbcStatement(MiniJdbcConnection connection) {
        this.connection = connection;
        connection.registerActiveStatement(this);
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
    public MiniJdbcConnection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.escapeProcessing = enable;
    }

    public boolean getEscapeProcessing() {
        return escapeProcessing;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0; // TODO
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        // TODO
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0; // TODO
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // TODO
    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0; // TODO
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0; // TODO
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // TODO
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null; // TODO
    }

    @Override
    public void clearWarnings() throws SQLException {
        // TODO
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // TODO
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return (currentResult != null ? currentResult.jdbcResultSet : null);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // TODO
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0; // TODO
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // TODO
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0; // TODO
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0; // TODO
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0; // TODO
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false; // TODO
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null; // TODO
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0; // TODO
    }

    @Override
    public void cancel() throws SQLException {
        // TODO
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        // TODO
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false; // TODO
    }


    protected void setCurrentResult(ResultHolder newResult) {
        currentResult = newResult;
    }
    
    protected ResultHolder getCurrentResult() {
        return currentResult;
    }
    
    
    protected static class ResultHolder {
        
        protected final MiniResult result;
        
        protected final MiniJdbcResultSet jdbcResultSet;
        
        
        protected ResultHolder(MiniResult result, MiniJdbcResultSet jdbcResultSet) {
            this.result = result;
            this.jdbcResultSet = jdbcResultSet;
        }

    }

}
