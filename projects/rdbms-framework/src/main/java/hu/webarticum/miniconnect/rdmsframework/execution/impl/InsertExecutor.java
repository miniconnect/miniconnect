package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.util.ResultUtil;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;

public class InsertExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockExclusively()) {
            return executeInternal(storageAccess, state, (InsertQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, InsertQuery insertQuery) {
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
        if (insertValueMap.size() != table.columns().names().size()) {
            return new StoredResult(new StoredError(
                    7,
                    "00007",
                    table.columns().names().size() + " values expected, but " + insertValueMap.size() + " found"));
        }
        
        try {
            TableQueryUtil.checkFields(table, insertValueMap.keySet());
        } catch (Exception e) {
            return new StoredResult(new StoredError(3, "00003", e.getMessage()));
        }

        BigInteger lastInsertId = null;
        if (autoIncrementedColumnHolder.isPresent()) {
            String columName = autoIncrementedColumnHolder.get().name();
            Object autoIncrementColumnValue = ResultUtil.resolveValue(insertValueMap.get(columName), state);
            if (autoIncrementColumnValue == null) {
                BigInteger autoValue = table.sequence().getAndIncrement();
                insertValueMap.put(columName, autoValue);
                lastInsertId = autoValue;
            }
        }
        
        System.out.println("BEFORE:");
        for (Map.Entry<String, Object> entry : insertValueMap.entrySet()) {
            Object value = entry.getValue();
            Class<?> clazz = value == null ? null : value.getClass();
            System.out.println(entry.getKey() + " = " + clazz + ":" + value);
        }
        System.out.println();
        
        Map<String, Object> convertedInsertValues = TableQueryUtil.convertColumnValues(table, insertValueMap, state);

        System.out.println("AFTER:");
        for (Map.Entry<String, Object> entry : convertedInsertValues.entrySet()) {
            Object value = entry.getValue();
            Class<?> clazz = value == null ? null : value.getClass();
            System.out.println(entry.getKey() + " = " + clazz + ":" + value);
        }
        System.out.println();
        
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
        
        if (lastInsertId != null) {
            state.setLastInsertId(lastInsertId);
        } else if (autoIncrementedColumnHolder.isPresent()) {
            String columName = autoIncrementedColumnHolder.get().name();
            Object convertedAutoIncrementColumnValue = convertedInsertValues.get(columName);
            BigInteger bigIntegerValue = TableQueryUtil.convert(convertedAutoIncrementColumnValue, BigInteger.class);
            table.sequence().ensureGreaterThan(bigIntegerValue);
        }
        
        return new StoredResult();
    }
    
}
