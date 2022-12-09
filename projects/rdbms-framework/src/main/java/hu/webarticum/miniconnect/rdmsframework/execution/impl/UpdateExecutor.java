package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch.TablePatchBuilder;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class UpdateExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(storageAccess, state, (UpdateQuery) query);
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, UpdateQuery updateQuery) {
        String schemaName = updateQuery.schemaName();
        String tableName = updateQuery.tableName();
        
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
        if (!table.isWritable()) {
            throw PredefinedError.TABLE_READONLY.toException(tableName);
        }

        Map<String, Object> queryUpdates = updateQuery.values();
        Map<String, Object> queryWhere = updateQuery.where();
        
        TableQueryUtil.checkFields(table, queryUpdates.keySet());
        TableQueryUtil.checkFields(table, queryWhere.keySet());

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
