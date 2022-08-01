package hu.webarticum.miniconnect.rdmsframework.engine;

import java.math.BigInteger;

public interface EngineSessionState {

    public String getCurrentSchema();

    public void setCurrentSchema(String schemaName);

    public BigInteger getLastInsertId();

    public void setLastInsertId(BigInteger lastInsertId);

    public Object getUserVariable(String variableName);

    public void setUserVariable(String variableName, Object value);

}
