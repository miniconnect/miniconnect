package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.InsertQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class InsertExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(storageAccess, state, (InsertQuery) query);
    }

    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, InsertQuery insertQuery) {
        String schemaName = insertQuery.schemaName();
        String tableName = insertQuery.tableName();
        
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

        ImmutableList<String> givenInsertFields = insertQuery.fields();
        ImmutableList<String> insertFields = givenInsertFields != null ? givenInsertFields : table.columns().names();
        ImmutableList<Object> insertValues = insertQuery.values();
        
        Map<String, Object> insertValueMap = insertFields .assign((v, i) -> insertValues.get(i)).toHashMap();

        Optional<Column> autoIncrementedColumnHolder = TableQueryUtil.getAutoIncrementedColumn(table);
        if (autoIncrementedColumnHolder.isPresent()) {
            String columName = autoIncrementedColumnHolder.get().name();
            if (!insertValueMap.containsKey(columName)) {
                insertValueMap.put(columName, null);
            }
        }
        
        // FIXME: currently default values are not supported
        int columnCount = table.columns().names().size();
        int givenCount = insertValueMap.size();
        if (givenCount != columnCount) {
            throw PredefinedError.COLUMN_COUNT_NOT_MATCHING.toException(columnCount, givenCount);
        }
        
        TableQueryUtil.checkFields(table, insertValueMap.keySet());

        LargeInteger lastInsertId = null;
        if (autoIncrementedColumnHolder.isPresent()) {
            String columName = autoIncrementedColumnHolder.get().name();
            Object autoIncrementColumnValue = ResultUtil.resolveValue(insertValueMap.get(columName), state);
            if (autoIncrementColumnValue == null) {
                LargeInteger autoValue = table.sequence().getAndIncrement();
                insertValueMap.put(columName, autoValue);
                lastInsertId = autoValue;
            }
        }
        
        Map<String, Object> convertedInsertValues =
                TableQueryUtil.convertColumnValues(table, insertValueMap, state, true);

        ImmutableMap<Integer, Object> values =
                TableQueryUtil.toByColumnPoisitionedImmutableMap(table, convertedInsertValues);
        List<Object> rowDataBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            if (!values.containsKey(i)) {
                String columnName = table.columns().names().get(i);
                throw PredefinedError.COLUMN_MISSING.toException(columnName);
            }
            rowDataBuilder.add(values.get(i));
        }
        ImmutableList<Object> rowData = ImmutableList.fromCollection(rowDataBuilder);
        
        TablePatch.TablePatchBuilder patchBuilder = TablePatch.builder();
        patchBuilder.insert(rowData);
        
        if (insertQuery.replace()) {
            Set<LargeInteger> conflictingRowIndices = collectConflictingRowIndices(convertedInsertValues, table);
            for (LargeInteger conflictingRowIndex : conflictingRowIndices) {
                patchBuilder.delete(conflictingRowIndex);
            }
        }
        
        TablePatch patch = patchBuilder.build();
        
        table.applyPatch(patch);
        
        if (lastInsertId != null) {
            state.setLastInsertId(lastInsertId);
        } else if (autoIncrementedColumnHolder.isPresent()) {
            String columName = autoIncrementedColumnHolder.get().name();
            Object convertedAutoIncrementColumnValue = convertedInsertValues.get(columName);
            LargeInteger largeIntegerValue = TableQueryUtil.convert(convertedAutoIncrementColumnValue, LargeInteger.class);
            table.sequence().ensureGreaterThan(largeIntegerValue);
        }
        
        return new StoredResult();
    }

    private Set<LargeInteger> collectConflictingRowIndices(Map<String, Object> convertedInsertValues, Table table) {
        Set<LargeInteger> result = new HashSet<>();
        NamedResourceStore<Column> columns = table.columns();
        for (Map.Entry<String, Object> entry : convertedInsertValues.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            String columnName = entry.getKey();
            ColumnDefinition definition = columns.get(columnName).definition();
            if (!definition.isUnique()) {
                continue;
            }
            
            result.addAll(TableQueryUtil.findAllNonNull(table, columnName, value));
        }
        return result;
    }

}
