package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.util.Map;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectCountQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class SelectCountExecutor implements ThrowingQueryExecutor {

    private static final String COLUMN_NAME = "COUNT";
    
    
    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(storageAccess, state, (SelectCountQuery) query);
    }
    
    private MiniResult executeInternal(
            StorageAccess storageAccess, EngineSessionState state, SelectCountQuery selectCountQuery) {
        String schemaName = selectCountQuery.schemaName();
        String tableName = selectCountQuery.tableName();
        
        if (schemaName == null) {
            schemaName = state.getCurrentSchema();
        }
        if (schemaName == null) {
            throw PredefinedError.SCHEMA_NOT_SELECTED.toException();
        }
        
        Schema schema = storageAccess.schemas().get(schemaName);
        if (schema == null) {
            throw PredefinedError.SCHEMA_NOT_FOUND.toException(schemaName);
        }
        
        Table table = schema.tables().get(tableName);
        if (table == null) {
            throw PredefinedError.TABLE_NOT_FOUND.toException(tableName);
        }
        
        Map<String, Object> queryWhere = selectCountQuery.where();
        if (queryWhere.isEmpty()) {
            return ResultUtil.createSingleValueResult(COLUMN_NAME, table.size());
        }
        
        Map<String, Object> convertedQueryWhere = TableQueryUtil.convertColumnValues(table, queryWhere, state, false);
        LargeInteger count = TableQueryUtil.countRows(table, convertedQueryWhere);
        return ResultUtil.createSingleValueResult(COLUMN_NAME, count);
    }
    
}
