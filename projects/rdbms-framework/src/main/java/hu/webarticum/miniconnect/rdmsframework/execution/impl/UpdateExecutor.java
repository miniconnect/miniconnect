package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch.TablePatchBuilder;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class UpdateExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockExclusively()) {
            return executeInternal(storageAccess, state, (UpdateQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, UpdateQuery updateQuery) {
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
                TableQueryUtil.convertColumnValues(table, queryUpdates, state, true);
        Map<String, Object> convertedQueryWhere = TableQueryUtil.convertColumnValues(table, queryWhere, state, false);

        List<LargeInteger> rowIndexes = TableQueryUtil.filterRowsToList(
                table, convertedQueryWhere, Collections.emptyList(), null);
        
        ImmutableMap<Integer, Object> updates =
                TableQueryUtil.toByColumnPoisitionedImmutableMap(table, convertedQueryUpdates);
        TablePatchBuilder patchBuilder = TablePatch.builder();
        rowIndexes.forEach(i -> patchBuilder.update(i, updates));
        TablePatch patch = patchBuilder.build();

        table.applyPatch(patch);

        Optional<Column> autoIncrementedColumnHolder = TableQueryUtil.getAutoIncrementedColumn(table);
        if (autoIncrementedColumnHolder.isPresent()) {
            String columnName = autoIncrementedColumnHolder.get().name();
            if (convertedQueryUpdates.containsKey(columnName)) {
                Object value = convertedQueryUpdates.get(columnName);
                LargeInteger largeIntegerValue = TableQueryUtil.convert(value, LargeInteger.class);
                table.sequence().ensureGreaterThan(largeIntegerValue);
            }
        }
        
        return new StoredResult();
    }
    
}
