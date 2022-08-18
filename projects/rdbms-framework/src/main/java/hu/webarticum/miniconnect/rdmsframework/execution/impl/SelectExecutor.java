package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniErrorException;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
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
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery.LeftJoinItem;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery.OrderByItem;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery.SelectItem;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery.WhereItem;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialCondition;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator.MultiComparatorBuilder;
import hu.webarticum.miniconnect.rdmsframework.util.TableQueryUtil;
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
        String tableAlias = selectQuery.tableAlias();
        ImmutableList<LeftJoinItem> leftJoins = selectQuery.leftJoins();
        
        LinkedHashMap<String, TableEntry> tableEntries = new LinkedHashMap<>();
        addTableInfo(
                tableEntries,
                tableAlias,
                schemaName,
                tableName,
                null,
                storageAccess,
                state);
        for (LeftJoinItem leftJoin : leftJoins) {
            addTableInfo(
                    tableEntries,
                    leftJoin.targetTableAlias(),
                    leftJoin.targetSchemaName(),
                    leftJoin.targetTableName(),
                    leftJoin,
                    storageAccess,
                    state);
        }
        
        List<SelectItemEntry> selectItemEntries = new ArrayList<>();
        ImmutableList<SelectItem> querySelectItems = selectQuery.selectItems();
        for (SelectItem querySelectItem : querySelectItems) {
            addSelectItemEntries(selectItemEntries, querySelectItem, tableEntries, storageAccess);
        }
        
        List<OrderByEntry> orderByEntries = new ArrayList<>();
        ImmutableList<OrderByItem> orderByItems = selectQuery.orderBy();
        for (OrderByItem orderByItem : orderByItems) {
            orderByEntries.add(toOrderByEntry(orderByItem, selectItemEntries, tableEntries));
        }
        
        ImmutableList<MiniColumnHeader> columnHeaders = selectItemEntries.stream()
                .map(this::columnHeaderOf)
                .collect(ImmutableList.createCollector());

        try {
            addFilters(selectQuery.where(), tableEntries);
        } catch (IncompatibleFiltersException e) {
            return new StoredResult(new StoredResultSetData(columnHeaders, ImmutableList.empty()));
        }

        Integer limit = selectQuery.limit();

        List<Map<String, BigInteger>> joinedRowIndices = collectRows(orderByEntries, limit, tableEntries);
        
        ImmutableList<ImmutableList<MiniValue>> data = joinedRowIndices.stream()
                .map(r -> selectRow(r, selectItemEntries, tableEntries))
                .collect(ImmutableList.createCollector());
        
        return new StoredResult(new StoredResultSetData(columnHeaders, data));
    }
    
    private void addTableInfo(
            LinkedHashMap<String, TableEntry> tableInfoMap,
            String alias,
            String schemaName,
            String tableName,
            LeftJoinItem leftJoin,
            StorageAccess storageAccess,
            EngineSessionState state) {
        if (schemaName == null) {
            schemaName = state.getCurrentSchema();
        }
        if (schemaName == null) {
            throw new MiniErrorException(new StoredError(5, "00005", "No schema is selected"));
        }
        
        Schema schema = storageAccess.schemas().get(schemaName);
        if (schema == null) {
            throw new MiniErrorException(new StoredError(4, "00004", "No such schema: " + schemaName));
        }
        
        Table table = schema.tables().get(tableName);
        if (table == null) {
            throw new MiniErrorException(new StoredError(2, "00002", "No such table: " + tableName));
        }
        
        if (alias == null) {
            alias = tableName;
        }
        
        if (tableInfoMap.containsKey(alias)) {
            throw new MiniErrorException(new StoredError(7, "00007", "Duplicated table alias: " + alias));
        }
        
        tableInfoMap.put(alias, new TableEntry(table, leftJoin));
    }
    
    private void addFilters(ImmutableList<WhereItem> whereItems, Map<String, TableEntry> tableEntries) {
        for (WhereItem whereItem : whereItems) {
            addFilter(whereItem, tableEntries);
        }
    }
    
    private void addFilter(WhereItem whereItem, Map<String, TableEntry> tableEntries) {
        String tableName = whereItem.tableName();
        if (tableName == null) {
            tableName = tableEntries.keySet().iterator().next();
        }
        
        TableEntry tableEntry = tableEntries.get(tableName);
        if (tableEntry == null) {
            throw new MiniErrorException(new StoredError(2, "00002", "No such table: " + tableName));
        }

        String fieldName = whereItem.fieldName();
        checkColumn(tableEntry.table, fieldName);
        
        Object value = whereItem.value();
        ColumnDefinition columnDefinition = tableEntry.table.columns().get(fieldName).definition();
        @SuppressWarnings("unchecked")
        Comparator<Object> comparator = (Comparator<Object>) columnDefinition.comparator();
        Object convertedValue = value;
        if (!(value instanceof SpecialCondition)) {
            convertedValue = TableQueryUtil.convert(value, columnDefinition.clazz());
        }
        Object existingValue = tableEntry.subFilter.get(fieldName);
        tableEntry.subFilter.put(fieldName, mergeFilterValue(existingValue, convertedValue, comparator));
    }
    
    private Object mergeFilterValue(Object existingValue, Object newValue, Comparator<Object> comparator) {
        if (existingValue == null) {
            return newValue;
        }
        
        if (
                !(existingValue instanceof SpecialCondition) &&
                !(newValue instanceof SpecialCondition) &&
                comparator.compare(newValue, existingValue) == 0) {
            return newValue;
        }
        
        if (existingValue == SpecialCondition.IS_NOT_NULL) {
            if (newValue == SpecialCondition.IS_NULL) {
                throw new IncompatibleFiltersException();
            }
            return newValue;
        } else if (existingValue == SpecialCondition.IS_NULL) {
            if (newValue != SpecialCondition.IS_NULL) {
                throw new IncompatibleFiltersException();
            }
            return newValue;
        } else {
            throw new IncompatibleFiltersException();
        }
    }
    
    private void addSelectItemEntries(
            List<SelectItemEntry> selectItemEntries,
            SelectItem querySelectItem,
            LinkedHashMap<String, TableEntry> tableInfoMap,
            StorageAccess storageAccess) {
        String tableName = querySelectItem.tableName();
        String fieldName = querySelectItem.fieldName();
        
        if (fieldName == null) {
            addSelectItemEntriesForWildcard(selectItemEntries, tableName, tableInfoMap, storageAccess);
            return;
        }
        
        String fieldAlias = querySelectItem.alias();

        if (fieldAlias == null) {
            fieldAlias = fieldName;
        }
        
        if (tableName == null) {
            tableName = tableInfoMap.keySet().iterator().next();
        } else if (!tableInfoMap.containsKey(tableName)) {
            throw new MiniErrorException(new StoredError(2, "00002", "No such table: " + tableName));
        }
        TableEntry tableEntry = tableInfoMap.get(tableName);
        checkColumn(tableEntry.table, fieldName);
        
        ValueTranslator valueTranslator = getValueTranslator(tableEntry, fieldName);
        ColumnDefinition columnDefinition = tableEntry.table.columns().get(fieldName).definition();
        selectItemEntries.add(new SelectItemEntry(tableName, fieldName, fieldAlias, valueTranslator, columnDefinition));
    }
    
    private void addSelectItemEntriesForWildcard(
            List<SelectItemEntry> selectItemEntries,
            String tableName,
            LinkedHashMap<String, TableEntry> tableInfoMap,
            StorageAccess storageAccess) {
        if (tableName == null) {
            for (String infoTableName : tableInfoMap.keySet()) {
                addSelectItemEntriesForWildcard(selectItemEntries, infoTableName, tableInfoMap, storageAccess);
            }
            return;
        }
        
        TableEntry tableEntry = tableInfoMap.get(tableName);
        Table table = tableEntry.table;
        NamedResourceStore<Column> columns = table.columns();
        for (String columnName : columns.names()) {
            ValueTranslator valueTranslator = getValueTranslator(tableEntry, columnName);
            ColumnDefinition columnDefinition = columns.get(columnName).definition();
            selectItemEntries.add(new SelectItemEntry(
                    tableName, columnName, columnName, valueTranslator, columnDefinition));
        }
    }

    private OrderByEntry toOrderByEntry(
            OrderByItem orderByItem,
            List<SelectItemEntry> selectItemEntries,
            LinkedHashMap<String, TableEntry> tableInfoMap) {
        Integer position = orderByItem.position();
        if (orderByItem.position() != null) {
            if (position < 1 || position > selectItemEntries.size()) {
                throw new MiniErrorException(new StoredError(8, "00008", "Invalid column position: " + position));
            }
            SelectItemEntry selectItemEntry = selectItemEntries.get(position - 1);
            return new OrderByEntry(selectItemEntry.tableAlias, selectItemEntry.fieldName, orderByItem.ascOrder());
        }
        
        String tableName = orderByItem.tableName();
        String fieldName = orderByItem.fieldName();
        
        if (tableName == null) {
            SelectItemEntry matchingSelectItemEntry = null;
            for (SelectItemEntry selectItemEntry : selectItemEntries) {
                if (selectItemEntry.fieldAlias.equals(fieldName)) {
                    matchingSelectItemEntry = selectItemEntry;
                    break;
                }
            }
            if (matchingSelectItemEntry != null) {
                return new OrderByEntry(
                        matchingSelectItemEntry.tableAlias, matchingSelectItemEntry.fieldName, orderByItem.ascOrder());
            } else {
                tableName = selectItemEntries.get(0).tableAlias;
            }
        }

        TableEntry tableEntry = tableInfoMap.get(tableName);
        if (tableEntry == null) {
            throw new MiniErrorException(new StoredError(2, "00002", "No such table: " + tableName));
        }
        checkColumn(tableEntry.table, fieldName);
        
        return new OrderByEntry(tableName, fieldName, orderByItem.ascOrder());
    }
    
    private void checkColumn(Table table, String columnName) {
        if (!table.columns().contains(columnName)) {
            throw new MiniErrorException(new StoredError(3, "00003", "No such column: " + columnName));
        }
    }
    
    private ValueTranslator getValueTranslator(TableEntry tableEntry, String fieldName) {
        return tableEntry.valueTranslators.computeIfAbsent(
                fieldName, k -> createValueTranslator(tableEntry.table, fieldName));
    }

    private ValueTranslator createValueTranslator(Table table, String fieldName) {
        checkColumn(table, fieldName);
        ColumnDefinition columnDefinition = table.columns().get(fieldName).definition();
        return createValueTranslator(columnDefinition);
    }

    private ValueTranslator createValueTranslator(ColumnDefinition columnDefinition) {
        Class<?> clazz = columnDefinition.clazz();
        Optional<StandardValueType> optional = StandardValueType.forClazz(clazz);
        if (optional.isPresent()) {
            return optional.get().defaultTranslator();
        }
        return JavaTranslator.of(clazz);
    }
    
    private MiniColumnHeader columnHeaderOf(SelectItemEntry selectItemEntry) {
        MiniValueDefinition valueDefinition = selectItemEntry.valueTranslator.definition();
        boolean isNullable = selectItemEntry.columnDefinition.isNullable();
        return new StoredColumnHeader(selectItemEntry.fieldAlias, isNullable, valueDefinition);
    }
    
    private ImmutableList<MiniValue> selectRow(
            Map<String, BigInteger> joinedRow,
            List<SelectItemEntry> selectItemEntries,
            Map<String, TableEntry> tableEntries) {
        List<MiniValue> resultBuilder = new ArrayList<>(selectItemEntries.size());
        for (SelectItemEntry selectItemEntry : selectItemEntries) {
            BigInteger rowIndex = joinedRow.get(selectItemEntry.tableAlias);
            TableEntry tableEntry = tableEntries.get(selectItemEntry.tableAlias);
            Object value = tableEntry.table.row(rowIndex).get(selectItemEntry.fieldName);
            MiniValue miniValue = selectItemEntry.valueTranslator.encodeFully(value);
            resultBuilder.add(miniValue);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private List<Map<String, BigInteger>> collectRows(
            List<OrderByEntry> orderByEntries, Integer limit, Map<String, TableEntry> tableEntries) {
        
        // TODO: handle easily optimizable cases
        
        return collectRowsInAGeneralUnoptimizedWay(orderByEntries, limit, tableEntries);
    }
    
    private List<Map<String, BigInteger>> collectRowsInAGeneralUnoptimizedWay(
            List<OrderByEntry> orderByEntries, Integer limit, Map<String, TableEntry> tableEntries) {
        List<String> remainingTableAliasList = new ArrayList<>(tableEntries.keySet());
        Map<String, BigInteger> joinedPrefix = new HashMap<>();
        List<Map<String, BigInteger>> result = new ArrayList<>();
        collectRowsFromNextTable(result, remainingTableAliasList, joinedPrefix, tableEntries);
        if (!orderByEntries.isEmpty()) {
            MultiComparator multiComparator = createJoinedMultiComparator(orderByEntries, tableEntries);
            result.sort((r1, r2) -> multiComparator.compare(
                    extractOrderValues(r1, orderByEntries, tableEntries),
                    extractOrderValues(r2, orderByEntries, tableEntries)));
        }
        if (limit != null) {
            int end = Math.min(limit, result.size());
            result = result.subList(0, end);
        }
        return result;
    }
    
    private void collectRowsFromNextTable(
            List<Map<String, BigInteger>> result,
            List<String> remainingTableAliasList,
            Map<String, BigInteger> joinedPrefix,
            Map<String, TableEntry> tableEntries) {
        boolean isLeaf = remainingTableAliasList.size() == 1;
        String tableAlias = remainingTableAliasList.get(0);
        TableEntry tableEntry = tableEntries.get(tableAlias);
        Map<String, Object> subFilter = new HashMap<>(tableEntry.subFilter);
        if (tableEntry.leftJoin != null) {
            String sourceTableAlias = tableEntry.leftJoin.sourceTableAlias();
            String sourceFieldName = tableEntry.leftJoin.sourceFieldName();
            String targetFieldName = tableEntry.leftJoin.targetFieldName();
            Table sourceTable = tableEntries.get(sourceTableAlias).table;
            BigInteger rowIndex = joinedPrefix.get(sourceTableAlias);
            Object joinValue = sourceTable.row(rowIndex).get(sourceFieldName);
            subFilter.put(targetFieldName, joinValue);
        }
        Iterator<BigInteger> rowIndexIterator = TableQueryUtil.filterRows(tableEntry.table, subFilter);
        while (rowIndexIterator.hasNext()) {
            BigInteger rowIndex = rowIndexIterator.next();
            Map<String, BigInteger> joinedRow = new HashMap<>(joinedPrefix);
            joinedRow.put(tableAlias, rowIndex);
            if (isLeaf) {
                result.add(joinedRow);
            } else {
                List<String> subRemainingTableAliasList =
                        remainingTableAliasList.subList(1, remainingTableAliasList.size());
                collectRowsFromNextTable(result, subRemainingTableAliasList, joinedRow, tableEntries);
            }
        }
    }

    private ImmutableList<Object> extractOrderValues(
            Map<String, BigInteger> joinedRow,
            List<OrderByEntry> orderByEntries,
            Map<String, TableEntry> tableEntries) {
        List<Object> result = new ArrayList<>(orderByEntries.size());
        Map<String, Row> rowCache = new HashMap<>();
        for (OrderByEntry orderByEntry : orderByEntries) {
            TableEntry tableEntry = tableEntries.get(orderByEntry.tableAlias);
            BigInteger rowIndex = joinedRow.get(orderByEntry.tableAlias);
            Row row = rowCache.computeIfAbsent(orderByEntry.tableAlias, a -> tableEntry.table.row(rowIndex));
            result.add(row.get(orderByEntry.fieldName));
        }
        return ImmutableList.fromCollection(result);
    }
    
    private MultiComparator createJoinedMultiComparator(
            List<OrderByEntry> orderByEntries, Map<String, TableEntry> tableEntries) {
        MultiComparatorBuilder builder = MultiComparator.builder();
        for (OrderByEntry orderByEntry : orderByEntries) {
            String columnName = orderByEntry.fieldName;
            TableEntry tableEntry = tableEntries.get(orderByEntry.tableAlias);
            ColumnDefinition columnDefinition = tableEntry.table.columns().get(columnName).definition();
            Comparator<?> columnComparator = columnDefinition.comparator();
            boolean nullable = columnDefinition.isNullable();
            builder.add(columnComparator, nullable, orderByEntry.ascOrder, true);
        }
        return builder.build();
    }
    
    
    private static class TableEntry {
        
        private final Table table;
        
        private final LeftJoinItem leftJoin;
        
        private final Map<String, ValueTranslator> valueTranslators = new HashMap<>();
        
        private final Map<String, Object> subFilter = new LinkedHashMap<>();
        
        
        private TableEntry(Table table, LeftJoinItem leftJoin) {
            this.table = table;
            this.leftJoin = leftJoin;
        }
        
    }
    
    
    private static class SelectItemEntry {

        private final String tableAlias;

        private final String fieldName;

        private final String fieldAlias;

        private final ValueTranslator valueTranslator;

        private final ColumnDefinition columnDefinition;
        
        
        private SelectItemEntry(
                String tableAlias,
                String fieldName,
                String fieldAlias,
                ValueTranslator valueTranslator,
                ColumnDefinition columnDefinition) {
            this.tableAlias = tableAlias;
            this.fieldName = fieldName;
            this.fieldAlias = fieldAlias;
            this.valueTranslator = valueTranslator;
            this.columnDefinition = columnDefinition;
        }

    }

    
    private static class OrderByEntry {

        private final String tableAlias;

        private final String fieldName;

        private final boolean ascOrder;
        
        
        private OrderByEntry(String tableAlias, String fieldName, boolean ascOrder) {
            this.tableAlias = tableAlias;
            this.fieldName = fieldName;
            this.ascOrder = ascOrder;
        }

    }
    
    
    private static class IncompatibleFiltersException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        
    }
    
}
