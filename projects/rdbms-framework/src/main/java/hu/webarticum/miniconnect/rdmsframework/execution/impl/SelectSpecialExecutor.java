package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectSpecialQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectableType;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SelectSpecialExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(state, (SelectSpecialQuery) query);
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
                throw PredefinedError.OTHER_ERROR.toException();
        }
    }

    private MiniResult createResult(String alias, String autoColumnName, Object content) {
        String columnName = alias != null ? alias : autoColumnName;
        return ResultUtil.createSingleValueResult(columnName, content);
    }
    
}
