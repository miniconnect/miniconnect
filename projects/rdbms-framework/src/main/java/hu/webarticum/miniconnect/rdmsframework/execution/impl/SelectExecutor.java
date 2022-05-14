package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.TableQueryUtil;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator.MultiComparatorBuilder;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SelectExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockShared()) {
            return executeInternal(storageAccess, state, (SelectQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, SelectQuery selectQuery) {
        String schemaName = selectQuery.schemaName();
        String tableName = selectQuery.tableName();
        
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
        
        Map<String, String> queryFields = selectQuery.fields();
        Map<String, Object> queryWhere = selectQuery.where();
        Map<String, Boolean> queryOrderBy = selectQuery.orderBy();
        Integer queryLimit = selectQuery.limit();
        
        if (queryFields.isEmpty()) {
            ImmutableList<String> columnNames = table.columns().names();
            queryFields = new LinkedHashMap<>(columnNames.size());
            for (String columnName : columnNames) {
                queryFields.put(columnName, columnName);
            }
        }
        
        try {
            TableQueryUtil.checkFields(table, queryFields.values());
            TableQueryUtil.checkFields(table, queryWhere.keySet());
            TableQueryUtil.checkFields(table, queryOrderBy.keySet());
        } catch (Exception e) {
            return new StoredResult(new StoredError(3, "00003", e.getMessage()));
        }
        
        Map<String, Object> convertedQueryWhere = TableQueryUtil.convertColumnValues(table, queryWhere);
        
        Integer unorderedLimit = queryOrderBy.isEmpty() ? queryLimit : null;
        List<BigInteger> rowIndexes = TableQueryUtil.filterRows(table, convertedQueryWhere, unorderedLimit);
        sortRowIndexes(table, rowIndexes, queryOrderBy);
        
        if (queryLimit != null && !queryOrderBy.isEmpty() && rowIndexes.size() > queryLimit) {
            rowIndexes = new ArrayList<>(rowIndexes.subList(0, queryLimit));
        }
        
        ImmutableList<ValueTranslator> valueTranslators = collectValueTranslators(table, queryFields);
        ImmutableList<ImmutableList<MiniValue>> data = selectData(table, valueTranslators, queryFields, rowIndexes);
        ImmutableList<MiniColumnHeader> columnHeaders = createColumnHeaders(table, valueTranslators, queryFields);
        
        return new StoredResult(new StoredResultSetData(columnHeaders, data));
    }
    
    private void sortRowIndexes(
            Table table, List<BigInteger> rowIndexes, Map<String, Boolean> queryOrderBy) {
        MultiComparator multiComparator = createMultiComparator(table, queryOrderBy);
        ImmutableList<String> orderColumnNames =
                ImmutableList.fromCollection(queryOrderBy.keySet());
        Function<BigInteger, ImmutableList<Object>> rowMapper =
                i -> orderColumnNames.map(n -> table.row(i).get(n));
        Comparator<BigInteger> rowIndexComparator =
                (i1, i2) -> multiComparator.compare(rowMapper.apply(i1), rowMapper.apply(i2));
        Collections.sort(rowIndexes, rowIndexComparator);
    }

    private MultiComparator createMultiComparator(Table table, Map<String, Boolean> queryOrderBy) {
        MultiComparatorBuilder builder = MultiComparator.builder();
        for (Map.Entry<String, Boolean> entry : queryOrderBy.entrySet()) {
            String columnName = entry.getKey();
            boolean asc = entry.getValue();
            ColumnDefinition columnDefinition = table.columns().get(columnName).definition();
            Comparator<?> columnComparator = columnDefinition.comparator();
            boolean nullable = columnDefinition.isNullable();
            builder.add(columnComparator, nullable, asc, true);
        }
        return builder.build();
    }
    
    private ImmutableList<ValueTranslator> collectValueTranslators(
            Table table, Map<String, String> queryFields) {
        return ImmutableList.fromCollection(queryFields.values())
                .map(n -> table.columns().get(n).definition())
                .map(this::createValueTranslator);
    }
    
    private ValueTranslator createValueTranslator(ColumnDefinition columnDefinition) {
        Class<?> clazz = columnDefinition.clazz();
        Optional<StandardValueType> optional = StandardValueType.forClazz(clazz);
        if (optional.isPresent()) {
            return optional.get().defaultTranslator();
        }
        return JavaTranslator.of(clazz);
    }

    private ImmutableList<MiniColumnHeader> createColumnHeaders(
            Table table,
            ImmutableList<ValueTranslator> valueTranslators,
            Map<String, String> queryFields) {
        ImmutableList<String> tableColumnNames =
                ImmutableList.fromCollection(queryFields.values());
        ImmutableList<String> resultColumnNames =
                ImmutableList.fromCollection(queryFields.keySet());
        return valueTranslators.map(
                (i, t) -> new StoredColumnHeader(
                        resultColumnNames.get(i),
                        table.columns().get(tableColumnNames.get(i)).definition().isNullable(),
                        t.definition()));
    }
    
    private ImmutableList<ImmutableList<MiniValue>> selectData(
            Table table,
            ImmutableList<ValueTranslator> valueTranslators,
            Map<String, String> queryFields,
            List<BigInteger> rowIndexes) {
        return ImmutableList.fromCollection(rowIndexes).map(
                i -> selectRow(table, valueTranslators, queryFields, i));
    }

    private ImmutableList<MiniValue> selectRow(
            Table table,
            ImmutableList<ValueTranslator> valueTranslators,
            Map<String, String> queryFields,
            BigInteger rowIndex) {
        return ImmutableList.fromCollection(queryFields.values())
                .map(c -> table.row(rowIndex).get(c))
                .map((i, v) -> valueTranslators.get(i).encodeFully(v));
    }
    
}
