package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.TableQueryUtil;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch.TablePatchBuilder;

public class SimpleUpdateExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockExclusively()) {
            return executeInternal(storageAccess, state, query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(
            StorageAccess storageAccess, EngineSessionState state, Query query) {
        UpdateQuery updateQuery = (UpdateQuery) query;
        String schemaName = updateQuery.schemaName();
        String tableName = updateQuery.tableName();
        
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
        if (!table.isWritable()) {
            return new StoredResult(new StoredError(6, "00006", "Table is read-only: " + tableName));
        }

        Map<String, Object> queryUpdates = updateQuery.values();
        Map<String, Object> queryWhere = updateQuery.where();

        try {
            TableQueryUtil.checkFields(table, queryUpdates.keySet());
            TableQueryUtil.checkFields(table, queryWhere.keySet());
        } catch (Exception e) {
            return new StoredResult(new StoredError(3, "00003", e.getMessage()));
        }

        Map<String, Object> convertedQueryUpdates =
                TableQueryUtil.convertColumnValues(table, queryUpdates);
        Map<String, Object> convertedQueryWhere =
                TableQueryUtil.convertColumnValues(table, queryWhere);
        
        List<BigInteger> rowIndexes = TableQueryUtil.filterRows(table, convertedQueryWhere, null);
        
        ImmutableMap<Integer, Object> updates =
                TableQueryUtil.toByColumnPoisitionedImmutableMap(table, convertedQueryUpdates);
        TablePatchBuilder patchBuilder = TablePatch.builder();
        rowIndexes.forEach(i -> patchBuilder.update(i, updates));
        TablePatch patch = patchBuilder.build();

        table.applyPatch(patch);
        
        return new StoredResult();
    }
    
}
