package hu.webarticum.miniconnect.jdbc;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResultSet;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.translator.BigintTranslator;

public abstract class AbstractJdbcStatement implements Statement {
    
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\b", Pattern.CASE_INSENSITIVE);
    
    
    private final MiniJdbcConnection connection;
    
    private volatile boolean escapeProcessing = false;
    
    private volatile ResultHolder currentResultHolder = null; // NOSONAR
    
    private volatile SQLWarning currentWarningHead = null; // NOSONAR
    
    private volatile BigInteger lastInsertedId = null;

    
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
        this.escapeProcessing = enable; // TODO: should be used in nativeSQL()
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
        return -1; // TODO: currently not supported
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
        // not supported
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
        BigintTranslator bigintTranslator = BigintTranslator.instance();
        MiniColumnHeader columnHeader = new StoredColumnHeader("GENERATED_KEYS", false, bigintTranslator.definition());
        ImmutableList<ImmutableList<MiniValue>> rows;
        if (lastInsertedId != null) {
            MiniValue resultValue = bigintTranslator.encodeFully(lastInsertedId);
            rows = ImmutableList.of(ImmutableList.of(resultValue));
        } else {
            rows = ImmutableList.empty();
        }
        StoredResultSetData data = new StoredResultSetData(ImmutableList.of(columnHeader), rows);
        return new MiniJdbcResultSet(this, new StoredResultSet(data)); // NOSONAR will be automatically closed
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
        return false; // not supported
    }


    protected void handleExecuteCompleted(String sql, ResultHolder resultHolder) {
        currentResultHolder = resultHolder;
        currentWarningHead = wrapWarnings(resultHolder.result.warnings());
        connection.setCurrentWarningHead(currentWarningHead);
        if (INSERT_PATTERN.matcher(sql).find()) {
            lastInsertedId = connection.getDatabaseProvider().getLastInsertedId(connection.getMiniSession());
        } else {
            lastInsertedId = null;
        }
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
