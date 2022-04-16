package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.InsertQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.TableQueryUtil;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;

public class SimpleInsertExecutor implements QueryExecutor {

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
        InsertQuery insertQuery = (InsertQuery) query;
        String schemaName = insertQuery.schemaName();
        String tableName = insertQuery.tableName();
        
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

        Map<String, Object> insertValues = insertQuery.values();

        try {
            TableQueryUtil.checkFields(table, insertValues.keySet());
        } catch (Exception e) {
            return new StoredResult(new StoredError(3, "00003", e.getMessage()));
        }

        // TODO: check that all values are set
        
        Map<String, Object> convertedInsertValues =
                TableQueryUtil.convertColumnValues(table, insertValues);

        ImmutableMap<Integer, Object> values =
                TableQueryUtil.toByColumnPoisitionedImmutableMap(table, convertedInsertValues);
        int columnCount = table.columns().names().size();
        List<Object> rowDataBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            if (!values.containsKey(i)) {
                return new StoredResult(new StoredError(7, "00007", "Missing column: " + i));
            }
            rowDataBuilder.add(values.get(i));
        }
        ImmutableList<Object> rowData = ImmutableList.fromCollection(rowDataBuilder);
        
        TablePatch patch =  TablePatch.builder().insert(rowData).build();
        
        table.applyPatch(patch);
        
        return new StoredResult();
    }
    
}
