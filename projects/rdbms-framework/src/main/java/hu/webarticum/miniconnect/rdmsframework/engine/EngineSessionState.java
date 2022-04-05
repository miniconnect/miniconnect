package hu.webarticum.miniconnect.rdmsframework.engine;

public interface EngineSessionState {

    public String getCurrentSchema();

    public void setCurrentSchema(String schemaName);

    public Object getUserVariable(String variableName);

    public void setUserVariable(String variableName, Object value);

}
