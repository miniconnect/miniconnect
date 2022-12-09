package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectValueQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;

public class SelectValueExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(state, (SelectValueQuery) query);
    }
    
    private MiniResult executeInternal(EngineSessionState state, SelectValueQuery selectValueQuery) {
        Object value = selectValueQuery.value();
        Object resolvedValue = ResultUtil.resolveValue(value, state);
        String alias = selectValueQuery.alias();
        String columnName = alias != null ? alias : ResultUtil.getAutoFieldNameFor(value);
        return ResultUtil.createSingleValueResult(columnName, resolvedValue);
    }

}
