package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SetVariableQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;

public class SetVariableExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(state, (SetVariableQuery) query);
    }
    
    private MiniResult executeInternal(EngineSessionState state, SetVariableQuery setVariableQuery) {
        String variableName = setVariableQuery.name();
        Object value = setVariableQuery.value();
        Object resolvedValue = ResultUtil.resolveValue(value, state);
        
        state.setUserVariable(variableName, resolvedValue);
        
        return new StoredResult();
    }

}
