package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectValueQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;

public class SelectValueExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockShared()) {
            return executeInternal(state, (SelectValueQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(EngineSessionState state, SelectValueQuery selectValueQuery) {
        Object value = selectValueQuery.value();
        Object resolvedValue = ResultUtil.resolveValue(value, state);
        String alias = selectValueQuery.alias();
        String columnName = alias != null ? alias : ResultUtil.getAutoFieldNameFor(value);
        return ResultUtil.createSingleValueResult(columnName, resolvedValue);
    }

}
