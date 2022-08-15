package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.math.BigInteger;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectCountQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class SelectCountExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockShared()) {
            return executeInternal(storageAccess, state, (SelectCountQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private MiniResult executeInternal(
            StorageAccess storageAccess, EngineSessionState state, SelectCountQuery selectCountQuery) {
        String schemaName = selectCountQuery.schemaName();
        String tableName = selectCountQuery.tableName();
        
        if (schemaName == null) {
            schemaName = state.getCurrentSchema();
        }
        if (schemaName == null) {
            return new StoredResult(new StoredError(5, "00005", "No schema is selected"));
        }
        
        Schema schema = storageAccess.schemas().get(schemaName);
        if (schema == null) {
            return new StoredResult(new StoredError(4, "00004", "No such schema: " + schemaName));
        }
        
        Table table = schema.tables().get(tableName);
        if (table == null) {
            return new StoredResult(new StoredError(2, "00002", "No such table: " + tableName));
        }
        
        String columnName = "COUNT";
        
        Map<String, Object> queryWhere = selectCountQuery.where();
        if (queryWhere.isEmpty()) {
            return ResultUtil.createSingleValueResult(columnName, table.size());
        }
        
        Map<String, Object> convertedQueryWhere = TableQueryUtil.convertColumnValues(table, queryWhere, state);
        BigInteger count = TableQueryUtil.countRows(table, convertedQueryWhere);
        return ResultUtil.createSingleValueResult(columnName, count);
    }
    
}
