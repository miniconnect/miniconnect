package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniSession;

public interface DatabaseProvider {

    public boolean isReadOnly(MiniSession session);

    public void setReadOnly(MiniSession session, boolean readOnly);
    
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
    
    public PreparedStatementProvider prepareStatement(MiniSession session, String sql);

    public String quoteString(String text);
    
    public String quoteIdentifier(String identifier);
    
    public String stringifyValue(ParameterValue parameterValue);
    
}
