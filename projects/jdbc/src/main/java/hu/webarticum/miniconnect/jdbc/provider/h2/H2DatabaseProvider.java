package hu.webarticum.miniconnect.jdbc.provider.h2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hu.webarticum.miniconnect.api.MiniErrorException;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.TransactionIsolationLevel;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;

public class H2DatabaseProvider implements DatabaseProvider {
    
    private static final String DATABASE_PRODUCT_NAME = "H2";
    
    private static final String SAVEPOINT_PREFIX = "MINICONNECT_H2_SAVEPOINT_";
    
    private static final Map<TransactionIsolationLevel, String> TRANSACTION_ISOLATION_LEVEL_NAME_MAP =
            new EnumMap<>(TransactionIsolationLevel.class);
    static {
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP.put(TransactionIsolationLevel.NONE, "");
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP.put(TransactionIsolationLevel.READ_UNCOMMITTED, "READ UNCOMMITTED");
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP.put(TransactionIsolationLevel.READ_COMMITTED, "READ COMMITTED");
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP.put(TransactionIsolationLevel.REPEATABLE_READ, "REPEATABLE READ");
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP.put(TransactionIsolationLevel.SERIALIZABLE, "SERIALIZABLE");
    }
    
    private static final Map<String, TransactionIsolationLevel> ADDITIONAL_TRANSACTION_ISOLATION_LEVELS =
            new HashMap<>();
    static {
        ADDITIONAL_TRANSACTION_ISOLATION_LEVELS.put("SNAPSHOT", TransactionIsolationLevel.READ_COMMITTED);
    }


    @Override
    public String getDatabaseProductName(MiniSession session) {
        return DATABASE_PRODUCT_NAME;
    }

    @Override
    public String getDatabaseFullVersion(MiniSession session) {
        String sql = "SELECT H2VERSION()";
        return extractSingleField(checkResult(session.execute(sql)), String.class);
    }

    @Override
    public int getDatabaseMajorVersion(MiniSession session) {
        String fullVersion = getDatabaseFullVersion(session);
        String[] tokens = fullVersion.split(".");
        try {
            return Integer.parseInt(tokens[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getDatabaseMinorVersion(MiniSession session) {
        String fullVersion = getDatabaseFullVersion(session);
        String[] tokens = fullVersion.split(".");
        try {
            return Integer.parseInt(tokens[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getUser(MiniSession session) {
        String sql = "SELECT CURRENT_USER()";
        return extractSingleField(checkResult(session.execute(sql)), String.class);
    }

    @Override
    public boolean isReadOnly(MiniSession session) {
        String sql = "SELECT READONLY()";
        return extractSingleField(checkResult(session.execute(sql)), Boolean.class);
    }

    @Override
    public void setReadOnly(MiniSession session, boolean readOnly) {
        boolean currentReadOnly = isReadOnly(session);
        if (readOnly != currentReadOnly) {
            throw new UnsupportedOperationException("Read-only mode can not be changed");
        }
    }
    
    @Override
    public String getSchema(MiniSession session) {
        String sql = "SELECT CURRENT_SCHEMA";
        return extractSingleField(checkResult(session.execute(sql)), String.class);
    }

    @Override
    public ImmutableList<String> getSchemas(MiniSession session) {
        String sql = "SHOW SCHEMAS";
        return extractSingleColumn(checkResult(session.execute(sql)), String.class);
        
    }

    @Override
    public void setSchema(MiniSession session, String schemaName) {
        String sql = "USE " + quoteIdentifier(schemaName);
        checkResult(session.execute(sql));
    }

    @Override
    public String getCatalog(MiniSession session) {
        String sql = "SELECT CURRENT_CATALOG";
        return extractSingleField(checkResult(session.execute(sql)), String.class);
    }

    @Override
    public void setCatalog(MiniSession session, String catalogName) {
        String currentCatalogName = getCatalog(session);
        if (!catalogName.equals(currentCatalogName)) {
            throw new UnsupportedOperationException("Catalog can not be changed");
        }
    }

    @Override
    public void checkSessionValid(MiniSession session) {
        getSchema(session);
    }

    @Override
    public boolean isAutoCommit(MiniSession session) {
        String sql = "SELECT AUTOCOMMIT()";
        return extractSingleField(checkResult(session.execute(sql)), Boolean.class);
    }

    @Override
    public void setAutoCommit(MiniSession session, boolean autoCommit) {
        String sql = "SET AUTOCOMMIT " + (autoCommit ? "ON" : "OFF");
        checkResult(session.execute(sql));
    }

    @Override
    public void commit(MiniSession session) {
        String sql = "COMMIT";
        checkResult(session.execute(sql));
    }

    @Override
    public void rollback(MiniSession session) {
        String sql = "ROLLBACK";
        checkResult(session.execute(sql));
    }

    @Override
    public int setSavepoint(MiniSession session) {
        int id = UUID.randomUUID().hashCode(); // FIXME
        String name = SAVEPOINT_PREFIX + id;
        String sql = "SAVEPOINT " + quoteIdentifier(name); // NOSONAR it's OK to repeat this
        checkResult(session.execute(sql));
        return id;
    }

    @Override
    public void setSavepoint(MiniSession session, String name) {
        String sql = "SAVEPOINT " + quoteIdentifier(name);
        checkResult(session.execute(sql));
    }

    @Override
    public void rollbackToSavepoint(MiniSession session, int id) {
        String name = SAVEPOINT_PREFIX + id;
        String sql = "SAVEPOINT " + quoteIdentifier(name);
        checkResult(session.execute(sql));
    }

    @Override
    public void rollbackToSavepoint(MiniSession session, String name) {
        String sql = "ROLLBACK TO SAVEPOINT " + quoteIdentifier(name);
        checkResult(session.execute(sql));
    }

    @Override
    public void releaseSavepoint(MiniSession session, int id) {
       // nothing to do
    }

    @Override
    public void releaseSavepoint(MiniSession session, String name) {
        // nothing to do
    }

    @Override
    public void setTransactionIsolationLevel(MiniSession session, TransactionIsolationLevel level) {
        String levelName = TRANSACTION_ISOLATION_LEVEL_NAME_MAP.get(level);
        String sql = "SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL " + levelName;
        checkResult(session.execute(sql));
    }

    @Override
    public TransactionIsolationLevel getTransactionIsolationLevel(MiniSession session) {
        String sql = "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE ID = SESSION_ID()";
        String levelName = extractSingleField(checkResult(session.execute(sql)), String.class);
        TransactionIsolationLevel level = ADDITIONAL_TRANSACTION_ISOLATION_LEVELS.get(levelName);
        if (level != null) {
            return level;
        }
        return TRANSACTION_ISOLATION_LEVEL_NAME_MAP.entrySet().stream() // NOSONAR: NoSuchElementException is OK
                .filter(e -> e.getValue().equals(levelName))
                .map(Map.Entry::getKey)
                .findAny()
                .get();
    }

    @Override
    public boolean isTransactionIsolationLevelSupported(MiniSession session, TransactionIsolationLevel level) {
        return true;
    }

    @Override
    public H2PreparedStatementProvider prepareStatement(MiniSession session, String sql) {
        return new H2PreparedStatementProvider(this, session, sql);
    }

    @Override
    public String quoteString(String text) {
        return "'" + text.replace("'", "''") + "'";
    }
    
    @Override
    public String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
    
    @Override
    public String stringifyValue(ParameterValue parameterValue) {
        Object value = parameterValue.value();
        if (value == null) {
            return "NULL";
        } else if (value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
            return value.toString();
        }
        
        return quoteString(value.toString());
    }
    
    private MiniResult checkResult(MiniResult result) {
        if (!result.success()) {
            throw new MiniErrorException(result.error());
        }
        
        return result;
    }
    
    private <T> T extractSingleField(MiniResult result, Class<T> clazz) {
        try (MiniResultSet resultSet = result.resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            return resultTable.iterator().next().get(0).as(clazz);
        }
    }

    private <T> ImmutableList<T> extractSingleColumn(MiniResult result, Class<T> clazz) {
        List<T> resultBuilder = new ArrayList<>();
        try (MiniResultSet resultSet = result.resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            for (ResultRecord resultRecord : resultTable) {
                T value = resultRecord.get(0).as(clazz);
                resultBuilder.add(value);
            }
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

}
