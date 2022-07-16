package hu.webarticum.miniconnect.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.lang.ImmutableList;

public abstract class AbstractJdbcStatement implements Statement {
    
    private final MiniJdbcConnection connection;
    
    private volatile boolean escapeProcessing = false;
    
    private volatile ResultHolder currentResultHolder = null; // NOSONAR
    
    private volatile SQLWarning currentWarningHead = null; // NOSONAR

    
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
        return 0; // not supported
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
       // not supported
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0; // not supported
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // not supported
    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0; // not supported
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // not supported
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0; // TODO
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
    public void setCursorName(String name) throws SQLException {
        // TODO
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return (currentResultHolder != null ? currentResultHolder.jdbcResultSet : null);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD) {
            throw createForwardOnlyException();
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // not supported
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0; // not supported
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false; // not supported
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false; // not supported
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null; // TODO
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public void cancel() throws SQLException {
        // not supported
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        // not supported
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;  // not supported
    }


    protected void handleExecuteCompleted(ResultHolder resultHolder) {
        currentResultHolder = resultHolder;
        currentWarningHead = wrapWarnings(resultHolder.result.warnings());
        connection.setCurrentWarningHead(currentWarningHead);
    }
    
    private SQLWarning wrapWarnings(ImmutableList<MiniError> miniWarnings) {
        if (miniWarnings.isEmpty()) {
            return null;
        }
        
        SQLWarning headWarning = wrapWarning(miniWarnings.get(0));
        SQLWarning parentWarning = headWarning;
        int length = miniWarnings.size();
        for (int i = 1; i < length; i++) {
            SQLWarning childWarning = wrapWarning(miniWarnings.get(i));
            parentWarning.setNextException(childWarning);
            parentWarning = childWarning;
        }
        return headWarning;
    }
    
    private SQLWarning wrapWarning(MiniError miniWarning) {
        return new SQLWarning(miniWarning.message(), miniWarning.sqlState(), miniWarning.code());
    }

    private SQLException createForwardOnlyException() {
        return new SQLException("This result set is FORWARD_ONLY");
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
