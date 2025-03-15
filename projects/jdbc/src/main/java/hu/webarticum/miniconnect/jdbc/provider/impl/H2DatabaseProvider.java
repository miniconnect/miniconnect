package hu.webarticum.miniconnect.jdbc.provider.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.TransactionIsolationLevel;

public class H2DatabaseProvider extends AbstractBlanketDatabaseProvider {
    
    private static final String DATABASE_PRODUCT_NAME = "H2";
    
    private static final String SAVEPOINT_PREFIX = "MINICONNECT_H2_SAVEPOINT_";
    
    private static final Map<String, TransactionIsolationLevel> ADDITIONAL_TRANSACTION_ISOLATION_LEVELS =
            new HashMap<>();
    static {
        ADDITIONAL_TRANSACTION_ISOLATION_LEVELS.put("SNAPSHOT", TransactionIsolationLevel.READ_COMMITTED);
    }


    @Override
    public String getDatabaseFullVersion(MiniSession session) {
        String sql = "SELECT H2VERSION()";
        return extractSingleField(checkResult(session.execute(sql)), String.class);
    }

    @Override
    public String getDatabaseProductName(MiniSession session) {
        return DATABASE_PRODUCT_NAME;
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

}
