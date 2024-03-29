package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.LargeInteger;

public interface DatabaseProvider {

    public String getDatabaseProductName(MiniSession session);

    public String getDatabaseFullVersion(MiniSession session);
    
    public int getDatabaseMajorVersion(MiniSession session);

    public int getDatabaseMinorVersion(MiniSession session);

    public String getUser(MiniSession session);

    public boolean isReadOnly(MiniSession session);

    public void setReadOnly(MiniSession session, boolean readOnly);

    public ImmutableList<String> getSchemas(MiniSession session);

    public String getSchema(MiniSession session);

    public void setSchema(MiniSession session, String schemaName);

    public String getCatalog(MiniSession session);

    public void setCatalog(MiniSession session, String catalogName);

    public void checkSessionValid(MiniSession session);

    public boolean isAutoCommit(MiniSession session);
    
    public void setAutoCommit(MiniSession session, boolean autoCommit);

    public void commit(MiniSession session);

    public void rollback(MiniSession session);

    public int setSavepoint(MiniSession session);

    public void setSavepoint(MiniSession session, String name);
    
    public void rollbackToSavepoint(MiniSession session, int id);
    
    public void rollbackToSavepoint(MiniSession session, String name);
    
    public void releaseSavepoint(MiniSession session, int id);
    
    public void releaseSavepoint(MiniSession session, String name);

    public void setTransactionIsolationLevel(MiniSession session, TransactionIsolationLevel level);

    public TransactionIsolationLevel getTransactionIsolationLevel(MiniSession session);

    public boolean isTransactionIsolationLevelSupported(MiniSession session, TransactionIsolationLevel level);

    public PreparedStatementProvider prepareStatement(MiniSession session, String sql);

    public LargeInteger getLastInsertedId(MiniSession session);
    
    public String quoteString(String text);
    
    public String quoteIdentifier(String identifier);
    
    public String stringifyValue(ParameterValue parameterValue);
    
}
