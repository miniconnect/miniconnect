package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.RangeSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelectionEntry;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SimpleSelectExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, Query query) {
        SelectQuery selectQuery = (SelectQuery) query;
        String tableName = selectQuery.tableName();
        Schema schema = storageAccess.schemas().get("default"); // FIXME
        Table table = schema.tables().get(tableName);

        if (table == null) {
            return new StoredResult(new StoredError(2, "00002", "No such table: " + tableName));
        }
        
        Map<String, String> queryFields = selectQuery.fields();
        Map<String, Object> queryWhere = selectQuery.where();
        Map<String, Boolean> queryOrderBy = selectQuery.orderBy();
        
        if (queryFields.isEmpty()) {
            queryFields = table.columns().names().assign(n -> n).toHashMap();
        }
        
        try {
            checkFields(table, queryFields.values());
            checkFields(table, queryWhere.keySet());
            checkFields(table, queryOrderBy.keySet());
        } catch (Exception e) {
            return new StoredResult(new StoredError(3, "00003", e.getMessage()));
        }
        
        List<BigInteger> rowIndexes = filterRows(table, queryWhere);
        sortRowIndexes(table, rowIndexes, queryOrderBy);
        ImmutableList<ValueTranslator> valueTranslators =
                collectValueTranslators(table, queryFields);
        ImmutableList<ImmutableList<MiniValue>> data =
                selectData(table, valueTranslators, queryFields, rowIndexes);
        ImmutableList<MiniColumnHeader> columnHeaders =
                createColumnHeaders(table, valueTranslators, queryFields);
        
        return new StoredResult(new StoredResultSetData(columnHeaders, data));
    }
    
    private void checkFields(Table table, Iterable<String> columnNames) {
        columnNames.forEach(n -> checkField(table, n));
    }

    private void checkField(Table table, String columnName) {
        if (!table.columns().contains(columnName)) {
            throw new IllegalArgumentException(String.format(
                    "No column '%s' in table '%s'", columnName, table));
        }
    }

    private List<BigInteger> filterRows(Table table, Map<String, Object> queryWhere) {
        Map<ImmutableList<String>, TableIndex> indexesByColumnName = new LinkedHashMap<>();
        Set<String> unindexedColumnNames =
                collectIndexes(table, queryWhere.keySet(), indexesByColumnName);

        TableSelection firstSelection = null;
        List<TableSelection> moreSelections = new ArrayList<>();
        for (Map.Entry<ImmutableList<String>, TableIndex> entry : indexesByColumnName.entrySet()) {
            ImmutableList<String> columnNames = entry.getKey();
            TableIndex tableIndex = entry.getValue();
            ImmutableList<Object> values = columnNames.map(queryWhere::get);
            TableSelection selection = tableIndex.find(values);
            if (firstSelection == null) {
                firstSelection = selection;
            } else {
                moreSelections.add(selection);
            }
        }
        if (firstSelection == null) {
            firstSelection = new RangeSelection(BigInteger.valueOf(0L), table.size());
        }
        
        return matchRows(table, queryWhere, firstSelection, moreSelections, unindexedColumnNames);
    }
    
    private List<BigInteger> matchRows(
            Table table, 
            Map<String, Object> queryWhere,
            TableSelection firstSelection,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames) {
        List<BigInteger> result = new ArrayList<>();
        for (TableSelectionEntry entry : firstSelection) {
            BigInteger rowIndex = entry.tableIndex();
            if (isRowMatchingWithMore(
                    table, rowIndex, queryWhere, moreSelections, unindexedColumnNames)) {
                result.add(rowIndex);
            }
        }
        return result;
    }
    
    private boolean isRowMatchingWithMore(
            Table table,
            BigInteger rowIndex,
            Map<String, Object> queryWhere,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames) {
        for (TableSelection selection : moreSelections) {
            if (!selection.containsRow(rowIndex)) {
                return false;
            }
        }
        
        if (!unindexedColumnNames.isEmpty()) {
            for (String columnName : unindexedColumnNames) {
                Object expectedValue = queryWhere.get(columnName);
                Object actualValue = table.columns().get(columnName).get(rowIndex);
                if (!Objects.equals(actualValue, expectedValue)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private Set<String> collectIndexes(
            Table table, Set<String> columnNames, Map<ImmutableList<String>, TableIndex> map) {
        NamedResourceStore<TableIndex> indexStore = table.indexes();
        ImmutableList<TableIndex> indexes = indexStore.names().map(indexStore::get);
        int maxIndexColumnCount = calculateMaxIndexColumnCount(indexes);
        int maxMatchingColumnCount = Math.min(columnNames.size(), maxIndexColumnCount);
        Set<String> result = new LinkedHashSet<>(columnNames);
        for (int columnCount = maxMatchingColumnCount; columnCount > 0; columnCount--) {
            for (TableIndex tableIndex : indexes) {
                ImmutableList<String> indexColumnNames = tableIndex.columnNames();
                if (areColumnsMatching(indexColumnNames, result, columnCount)) {
                    ImmutableList<String> matchedColumnNames =
                            indexColumnNames.section(0, columnCount);
                    map.put(matchedColumnNames, tableIndex);
                    result.removeAll(matchedColumnNames.asList());
                }
            }
        }
        return result;
    }
    
    private boolean areColumnsMatching(
            ImmutableList<String> indexColumnNames,
            Set<String> availableColumnNames,
            int columnCount) {
        
        if (indexColumnNames.size() < columnCount) {
            return false;
        }
        
        for (int i = 0; i < columnCount; i++) {
            String columnName = indexColumnNames.get(i);
            if (!availableColumnNames.contains(columnName)) {
                return false;
            }
        }
        
        return true;
    }

    private int calculateMaxIndexColumnCount(ImmutableList<TableIndex> indexes) {
        int maxIndexColumnCount = 0;
        for (TableIndex tableIndex : indexes) {
            int indexColumnCount = tableIndex.columnNames().size();
            if (indexColumnCount > maxIndexColumnCount) {
                maxIndexColumnCount = indexColumnCount;
            }
        }
        return maxIndexColumnCount;
    }
    
    private void sortRowIndexes(
            Table table, List<BigInteger> rowIndexes, Map<String, Boolean> queryOrderBy) {
        MultiComparator multiComparator = createMultiComparator(table, queryOrderBy);
        ImmutableList<String> orderColumnNames =
                ImmutableList.fromCollection(queryOrderBy.keySet());
        Function<BigInteger, ImmutableList<Object>> rowMapper =
                i -> orderColumnNames.map(n -> table.columns().get(n).get(i));
        Comparator<BigInteger> rowIndexComparator =
                (i1, i2) -> multiComparator.compare(rowMapper.apply(i1), rowMapper.apply(i2));
        Collections.sort(rowIndexes, rowIndexComparator);
    }
    
    private MultiComparator createMultiComparator(Table table, Map<String, Boolean> queryOrderBy) {
        List<Comparator<?>> comparators = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : queryOrderBy.entrySet()) {
            String columnName = entry.getKey();
            boolean asc = entry.getValue();
            Comparator<?> columnComparator =
                    table.columns().get(columnName).definition().comparator();
            Comparator<?> directedComparator = asc ? columnComparator : columnComparator.reversed();
            comparators.add(directedComparator);
        }
        return new MultiComparator(comparators);
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
        return valueTranslators.mapIndex(
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
                .map(c -> table.columns().get(c).get(rowIndex))
                .mapIndex((i, v) -> valueTranslators.get(i).encodeFully(v));
    }
    
}
