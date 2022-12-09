package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.DeleteQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch.TablePatchBuilder;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class DeleteExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(storageAccess, state, (DeleteQuery) query);
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, DeleteQuery deleteQuery) {
        String schemaName = deleteQuery.schemaName();
        String tableName = deleteQuery.tableName();
        
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

        Map<String, Object> queryWhere = deleteQuery.where();

        TableQueryUtil.checkFields(table, queryWhere.keySet());

        Map<String, Object> convertedQueryWhere = TableQueryUtil.convertColumnValues(table, queryWhere, state, false);
        
        List<LargeInteger> rowIndexes = TableQueryUtil.filterRowsToList(
                table, convertedQueryWhere, Collections.emptyList(), null);
        
        TablePatchBuilder patchBuilder = TablePatch.builder();
        rowIndexes.forEach(patchBuilder::delete);
        TablePatch patch = patchBuilder.build();

        table.applyPatch(patch);
        
        return new StoredResult();
    }

}
