package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.ParameterValue;

public interface DatabaseProvider {

    public boolean isReadOnly(MiniSession session);

    public void setReadOnly(MiniSession session, boolean readOnly);
    
    public String getSchema(MiniSession session);

    public void setSchema(MiniSession session, String schemaName);

    public String getCatalog(MiniSession session);

    public void setCatalog(MiniSession session, String catalogName);

    public void checkSessionValid(MiniSession session);

    public PreparedStatementProvider prepareStatement(MiniSession session, String sql);

    public String quoteIdentifier(String identifier);
    
    public String stringifyValue(ParameterValue parameterValue);
    
}
