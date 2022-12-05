package hu.webarticum.miniconnect.rdmsframework.engine.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;

public class SimpleEngineSessionState implements EngineSessionState {

    private volatile String currentSchema = null;

    private volatile LargeInteger lastInsertId = null;

    private final Map<String, Object> userVariables = Collections.synchronizedMap(new HashMap<>());
    
    
    @Override
    public String getCurrentSchema() {
        return currentSchema;
    }

    @Override
    public void setCurrentSchema(String schemaName) {
        this.currentSchema = schemaName;
    }

    @Override
    public LargeInteger getLastInsertId() {
        return lastInsertId;
    }

    @Override
    public void setLastInsertId(LargeInteger lastInsertId) {
        this.lastInsertId = lastInsertId;
    }

    @Override
    public Object getUserVariable(String variableName) {
        return userVariables.get(variableName);
    }

    @Override
    public void setUserVariable(String variableName, Object value) {
        if (value != null) {
            userVariables.put(variableName, value);
        } else {
            userVariables.remove(variableName);
        }
    }
    
}
