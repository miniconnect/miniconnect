package hu.webarticum.miniconnect.rdmsframework.engine;

import hu.webarticum.miniconnect.lang.LargeInteger;

public interface EngineSessionState {

    public String getCurrentSchema();

    public void setCurrentSchema(String schemaName);

    public LargeInteger getLastInsertId();

    public void setLastInsertId(LargeInteger lastInsertId);

    public Object getUserVariable(String variableName);

    public void setUserVariable(String variableName, Object value);

}
