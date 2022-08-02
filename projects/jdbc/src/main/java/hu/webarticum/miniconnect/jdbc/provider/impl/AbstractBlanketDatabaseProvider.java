package hu.webarticum.miniconnect.jdbc.provider.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

public abstract class AbstractBlanketDatabaseProvider implements DatabaseProvider {

    protected static final Map<TransactionIsolationLevel, String> TRANSACTION_ISOLATION_LEVEL_NAME_MAP;
    static {
        Map<TransactionIsolationLevel, String> mapBuilder = new EnumMap<>(TransactionIsolationLevel.class);
        mapBuilder.put(TransactionIsolationLevel.NONE, "");
        mapBuilder.put(TransactionIsolationLevel.READ_UNCOMMITTED, "READ UNCOMMITTED");
        mapBuilder.put(TransactionIsolationLevel.READ_COMMITTED, "READ COMMITTED");
        mapBuilder.put(TransactionIsolationLevel.REPEATABLE_READ, "REPEATABLE READ");
        mapBuilder.put(TransactionIsolationLevel.SERIALIZABLE, "SERIALIZABLE");
        TRANSACTION_ISOLATION_LEVEL_NAME_MAP = Collections.unmodifiableMap(mapBuilder);
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
    public boolean isTransactionIsolationLevelSupported(MiniSession session, TransactionIsolationLevel level) {
        return true;
    }

    @Override
    public BlanketFakePreparedStatementProvider prepareStatement(MiniSession session, String sql) {
        return new BlanketFakePreparedStatementProvider(this, session, sql);
    }

    @Override
    public BigInteger getLastInsertedId(MiniSession session) {
        String sql = "CALL IDENTITY()";
        return extractSingleField(checkResult(session.execute(sql)), BigInteger.class);
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
    
    
    protected MiniResult checkResult(MiniResult result) {
        if (!result.success()) {
            throw new MiniErrorException(result.error());
        }
        
        return result;
    }
    
    protected <T> T extractSingleField(MiniResult result, Class<T> clazz) {
        try (MiniResultSet resultSet = result.resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            return resultTable.iterator().next().get(0).as(clazz);
        }
    }

    protected <T> ImmutableList<T> extractSingleColumn(MiniResult result, Class<T> clazz) {
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
