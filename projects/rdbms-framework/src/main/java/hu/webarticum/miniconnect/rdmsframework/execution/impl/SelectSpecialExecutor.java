package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectSpecialQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectableType;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SelectSpecialExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockShared()) {
            return executeInternal(state, (SelectSpecialQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(EngineSessionState state, SelectSpecialQuery selectSpecialQuery) {
        StandardValueType.forClazz(String.class).get().defaultTranslator().definition(); // NOSONAR String is built-in
        SpecialSelectableType queryType = selectSpecialQuery.queryType();
        String alias = selectSpecialQuery.alias();
        switch (queryType) {
            case CURRENT_USER:
                return createResult(alias, "CURRENT_USER", "");
            case CURRENT_SCHEMA:
                return createResult(alias, "CURRENT_SCHEMA", state.getCurrentSchema());
            case CURRENT_CATALOG:
                return createResult(alias, "CURRENT_CATALOG", state.getCurrentSchema());
            case READONLY:
                return createResult(alias, "READONLY", false);
            case AUTOCOMMIT:
                return createResult(alias, "AUTOCOMMIT", true);
            case LAST_INSERT_ID:
                return createResult(alias, "LAST_INSERT_ID", state.getLastInsertId());
            default:
                return new StoredResult(new StoredError(42, "00042", "No luck, sorry"));
        }
    }

    private MiniResult createResult(String alias, String autoColumnName, Object content) {
        String columnName = alias != null ? alias : autoColumnName;
        return ResultUtil.createSingleValueResult(columnName, content);
    }
    
}
