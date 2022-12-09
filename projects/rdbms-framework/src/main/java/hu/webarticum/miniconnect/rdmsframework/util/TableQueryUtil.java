package hu.webarticum.miniconnect.rdmsframework.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.impl.select.OrderByEntry;
import hu.webarticum.miniconnect.rdmsframework.query.NullsOrderMode;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialCondition;
import hu.webarticum.miniconnect.rdmsframework.query.VariableValue;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.RangeSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.SortMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator.MultiComparatorBuilder;
import hu.webarticum.miniconnect.record.converter.DefaultConverter;
import hu.webarticum.miniconnect.util.FilteringIterator;
import hu.webarticum.miniconnect.util.GroupingIterator;
import hu.webarticum.miniconnect.util.LimitingIterator;

public class TableQueryUtil {
    
    // TODO: move this to StorageAccess or similar place
    private static final DefaultConverter CONVERTER = new DefaultConverter();
    

    private TableQueryUtil() {
        // utility class
    }


    public static void checkFields(Table table, Iterable<String> columnNames) {
        columnNames.forEach(n -> checkField(table, n));
    }

    public static void checkField(Table table, String columnName) {
        if (!table.columns().contains(columnName)) {
            throw PredefinedError.COLUMN_NOT_FOUND.toException(table.name(), columnName);
        }
    }

    public static LargeInteger countRows(Table table, Map<String, Object> queryWhere) {
        Map<ImmutableList<String>, TableIndex> indexesByColumnName = new LinkedHashMap<>();
        Set<String> unindexedColumnNames = collectIndexes(table, queryWhere.keySet(), indexesByColumnName);
        
        List<TableSelection> moreSelections = new ArrayList<>();
        TableSelection firstSelection = collectIndexSelections(
                table.size(), queryWhere, Collections.emptyList(), indexesByColumnName, moreSelections);

        LargeInteger result = LargeInteger.ZERO;
        for (LargeInteger rowIndex : firstSelection) {
            if (isRowMatchingWithMore(table, rowIndex, queryWhere, moreSelections, unindexedColumnNames)) {
                result = result.add(LargeInteger.ONE);
            }
        }
        return result;
    }

    public static List<LargeInteger> filterRowsToList(
            Table table, Map<String, Object> filter, List<OrderByEntry> orderBy, LargeInteger limit) {
        Iterator<LargeInteger> iterator = filterRows(table, filter, orderBy, limit);
        return collectIterator(iterator);
    }
    
    public static Iterator<LargeInteger> filterRows(
            Table table, Map<String, Object> filter, List<OrderByEntry> orderBy, LargeInteger limit) {
        Set<String> filterIndexColumns = new HashSet<>(filter.keySet());
        List<OrderByEntry> matchedOrderByEntries = new ArrayList<>();
        List<String> matchedFilterColumns = new ArrayList<>();
        TableIndex orderIndex = findOrderIndex(
                table, orderBy, filterIndexColumns, matchedOrderByEntries, matchedFilterColumns);
        if (orderIndex != null) {
            filterIndexColumns.removeAll(matchedFilterColumns);
        }
        Map<ImmutableList<String>, TableIndex> indexesByColumnName = new LinkedHashMap<>();
        Set<String> unindexedColumnNames = collectIndexes(table, filterIndexColumns, indexesByColumnName);
        indexesByColumnName = prependIndex(matchedFilterColumns, orderIndex, indexesByColumnName);

        List<TableSelection> moreSelections = new ArrayList<>();
        TableSelection firstSelection = collectIndexSelections(
                table.size(), filter, matchedOrderByEntries, indexesByColumnName, moreSelections);
        
        Iterator<LargeInteger> result = matchRows(table, filter, firstSelection, moreSelections, unindexedColumnNames);
        
        if (!orderBy.isEmpty() && orderIndex == null) {
            List<LargeInteger> resultList = collectIterator(result);
            MultiComparator rowComparator = createMultiComparator(orderBy, s -> table);
            Comparator<LargeInteger> rowIndexComparator = createRowIndexComparator(rowComparator, table, orderBy);
            resultList.sort(rowIndexComparator);
            if (limit != null) {
                int intLimit = limit.intValueExact();
                resultList = resultList.subList(0, intLimit);
            }
            result = resultList.iterator();
        } else if (matchedOrderByEntries.size() < orderBy.size()) {
            MultiComparator outerRowComparator = createMultiComparator(matchedOrderByEntries, s -> table);
            Comparator<LargeInteger> outerRowIndexComparator = createRowIndexComparator(
                    outerRowComparator, table, orderBy);
            MultiComparator innerRowComparator = createMultiComparator(matchedOrderByEntries, s -> table);
            Comparator<LargeInteger> innerRowIndexComparator = createRowIndexComparator(
                    innerRowComparator, table, orderBy);
            result = new GroupingIterator<>(result, outerRowIndexComparator, (List<LargeInteger> groupItems) -> {
                groupItems.sort(innerRowIndexComparator);
                return groupItems;
            });
            if (limit != null) {
                result = new LimitingIterator<>(result, limit);
            }
        } else if (limit != null) {
            result = new LimitingIterator<>(result, limit);
        }
        
        return result;
    }
    
    private static Comparator<LargeInteger> createRowIndexComparator(
            MultiComparator rowComparator, Table table, List<OrderByEntry> orderByEntries) {
        return (i1, i2) -> rowComparator.compare(
                TableQueryUtil.extractOrderValues(orderByEntries, s -> table, s -> i1),
                TableQueryUtil.extractOrderValues(orderByEntries, s -> table, s -> i2));
    }
    
    private static TableIndex findOrderIndex(
            Table table,
            List<OrderByEntry> orderBy,
            Set<String> filterIndexColumns,
            List<OrderByEntry> matchedOrderByEntries,
            List<String> matchedFilterColumns) {
        if (orderBy.isEmpty()) {
            return null;
        }
        
        Set<String> columnNames = filterIndexColumns;
        if (columnNames.isEmpty()) {
            columnNames = new HashSet<>(table.columns().names().asList());
        }
        
        List<OrderByEntry> indexableOrderByEntries = new ArrayList<>();
        for (OrderByEntry orderByEntry : orderBy) {
            if (!columnNames.contains(orderByEntry.fieldName)) {
                break;
            }
            indexableOrderByEntries.add(orderByEntry);
        }
        if (indexableOrderByEntries.isEmpty()) {
            return null;
        }

        List<OrderByEntry> maxOrderByEntries = new ArrayList<>();
        List<String> maxfilterColumns = new ArrayList<>();
        TableIndex result = null;
        for (TableIndex tableIndex : table.indexes().resources()) {
            List<OrderByEntry> orderByEntriesOut = new ArrayList<>();
            List<String> filterColumnsOut = new ArrayList<>();
            if (matchOrderByIndex(
                    tableIndex, indexableOrderByEntries, columnNames, orderByEntriesOut, filterColumnsOut)) {
                int matchLength = orderByEntriesOut.size();
                int filterCount = filterColumnsOut.size();
                int maxMatchLength = maxOrderByEntries.size();
                int maxMatchMaxFilterCount = maxfilterColumns.size();
                if (
                        matchLength > maxMatchLength ||
                        (matchLength == maxMatchLength && filterCount > maxMatchMaxFilterCount)) {
                    maxOrderByEntries = orderByEntriesOut;
                    maxfilterColumns = filterColumnsOut;
                }
            }
        }
        matchedOrderByEntries.addAll(maxOrderByEntries);
        matchedFilterColumns.addAll(maxfilterColumns);
        return result;
    }
    
    private static boolean matchOrderByIndex(
            TableIndex tableIndex,
            List<OrderByEntry> orderByEntries,
            Set<String> columnNames,
            List<OrderByEntry> orderByEntriesOut,
            List<String> filterColumnsOut) {
        boolean result = false;
        ImmutableList<String> indexColumnNames = tableIndex.columnNames();
        
        int indexLength = indexColumnNames.size();
        int orderLength = Math.min(indexLength, orderByEntries.size());
        for (int i = 0; i < orderLength; i++) {
            String indexColumnName = indexColumnNames.get(i);
            OrderByEntry orderByEntry = orderByEntries.get(i);
            if (!orderByEntry.fieldName.equals(indexColumnName)) {
                return result;
            }
            result = true;
            orderByEntriesOut.add(orderByEntry);
            filterColumnsOut.add(orderByEntry.fieldName);
        }
        
        for (int i = orderLength; i < indexLength; i++) {
            String indexColumnName = indexColumnNames.get(i);
            if (!columnNames.contains(indexColumnName)) {
                break;
            }
            filterColumnsOut.add(indexColumnName);
        }
        
        return result;
    }
    
    private static Map<ImmutableList<String>, TableIndex> prependIndex(
            List<String> columnNames, TableIndex index, Map<ImmutableList<String>, TableIndex> indexes) {
        if (index == null) {
            return indexes;
        }
        
        Map<ImmutableList<String>, TableIndex> result = new LinkedHashMap<>();
        result.put(ImmutableList.fromCollection(columnNames), index);
        result.putAll(indexes);
        return result;
    }
    
    private static Set<String> collectIndexes(
            Table table, Set<String> columnNames, Map<ImmutableList<String>, TableIndex> map) {
        NamedResourceStore<TableIndex> indexStore = table.indexes();
        ImmutableList<TableIndex> indexes = indexStore.resources();
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

    private static TableSelection collectIndexSelections(
            LargeInteger tableSize,
            Map<String, Object> filter,
            List<OrderByEntry> orderBy,
            Map<ImmutableList<String>, TableIndex> indexesByColumnName,
            List<TableSelection> moreSelections) {
        if (filter.containsValue(null)) {
            return new SimpleSelection(ImmutableList.empty());
        }
        
        TableSelection firstSelection = null;
        for (Map.Entry<ImmutableList<String>, TableIndex> entry : indexesByColumnName.entrySet()) {
            ImmutableList<String> columnNames = entry.getKey();
            TableIndex tableIndex = entry.getValue();
            ImmutableList<Object> values = columnNames.map(filter::get);
            ImmutableList<SortMode> sortModes;
            if (firstSelection == null && !orderBy.isEmpty()) {
                sortModes = values.map((i, v) -> getIndexNthSortMode(i, orderBy));
            } else {
                sortModes = values.map(v -> SortMode.UNSORTED);
            }
            TableSelection selection = tableIndex.findMulti(
                    values.map(v -> v instanceof SpecialCondition ? null : v),
                    InclusionMode.INCLUDE,
                    values.map(v -> v instanceof SpecialCondition ? null : v),
                    InclusionMode.INCLUDE,
                    values.map(TableQueryUtil::nullsModeForValue),
                    sortModes);
            if (firstSelection == null) {
                firstSelection = selection;
            } else {
                moreSelections.add(selection);
            }
        }
        if (firstSelection == null) {
            firstSelection = new RangeSelection(LargeInteger.ZERO, tableSize);
        }
        
        return firstSelection;
    }
    
    private static NullsMode nullsModeForValue(Object value) {
        if (value == SpecialCondition.IS_NULL) {
            return NullsMode.NULLS_ONLY;
        } else if (value == SpecialCondition.IS_NOT_NULL) {
            return NullsMode.NO_NULLS;
        } else {
            return NullsMode.WITH_NULLS;
        }
    }
    
    private static SortMode getIndexNthSortMode(int i, List<OrderByEntry> orderBy) {
        if (orderBy.size() <= i) {
            return SortMode.UNSORTED;
        }
        
        return getSortModeOf(orderBy.get(i));
    }
    
    private static SortMode getSortModeOf(OrderByEntry orderByEntry) {
        boolean nullsFirst;
        if (orderByEntry.nullsOrderMode == NullsOrderMode.NULLS_AUTO) {
            nullsFirst = orderByEntry.ascOrder;
        } else {
            nullsFirst = (orderByEntry.nullsOrderMode == NullsOrderMode.NULLS_FIRST);
        }
        
        if (orderByEntry.ascOrder) {
            return nullsFirst ? SortMode.ASC_NULLS_FIRST : SortMode.ASC_NULLS_LAST;
        } else {
            return nullsFirst ? SortMode.DESC_NULLS_FIRST : SortMode.DESC_NULLS_LAST;
        }
    }
    
    private static Iterator<LargeInteger> matchRows(
            Table table,
            Map<String, Object> queryWhere,
            TableSelection firstSelection,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames) {
        if (moreSelections.isEmpty() && unindexedColumnNames.isEmpty()) {
            return firstSelection.iterator();
        }
        
        return new FilteringIterator<>(
                firstSelection.iterator(),
                rowIndex -> isRowMatchingWithMore(table, rowIndex, queryWhere, moreSelections, unindexedColumnNames));
    }

    private static boolean isRowMatchingWithMore(
            Table table,
            LargeInteger rowIndex,
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
                Column column = table.columns().get(columnName);
                Object expectedValue = queryWhere.get(columnName);
                Object actualValue = table.row(rowIndex).get(columnName);
                if (!isValueMatching(expectedValue, actualValue, column)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static boolean isValueMatching(Object expectedValue, Object actualValue, Column column) {
        if (expectedValue == SpecialCondition.IS_NULL) {
            return actualValue == null;
        } else if (expectedValue == SpecialCondition.IS_NOT_NULL) {
            return actualValue != null;
        }
        
        @SuppressWarnings("unchecked")
        Comparator<Object> comparator = (Comparator<Object>) column.definition().comparator();
        return comparator.compare(actualValue, expectedValue) == 0;
    }

    private static boolean areColumnsMatching(
            ImmutableList<String> indexColumnNames, Set<String> availableColumnNames, int columnCount) {
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

    private static int calculateMaxIndexColumnCount(ImmutableList<TableIndex> indexes) {
        int maxIndexColumnCount = 0;
        for (TableIndex tableIndex : indexes) {
            int indexColumnCount = tableIndex.columnNames().size();
            if (indexColumnCount > maxIndexColumnCount) {
                maxIndexColumnCount = indexColumnCount;
            }
        }
        return maxIndexColumnCount;
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object source, Class<T> targetClazz) {
        return (T) CONVERTER.convert(source, targetClazz);
    }

    public static Map<String, Object> convertColumnValues(
            Table table, Map<String, Object> columnValues, EngineSessionState state, boolean check) {
        Map<String, Object> result = new LinkedHashMap<>();
        NamedResourceStore<Column> columns = table.columns();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            Object convertedValue = value;
            if (convertedValue instanceof VariableValue) {
                String variableName = ((VariableValue) value).name();
                convertedValue = state.getUserVariable(variableName);
            }
            if (!(convertedValue instanceof SpecialCondition)) {
                ColumnDefinition definition = columns.get(columnName).definition();
                Class<?> columnClazz = definition.clazz();
                convertedValue = convert(convertedValue, columnClazz);
                
                // FIXME currently, null check is performed in applyPatch()
                if (check && convertedValue != null) {
                    Optional<ImmutableList<Object>> enumValuesOptional = definition.enumValues();
                    if (enumValuesOptional.isPresent()) {
                        ImmutableList<Object> enumValues = enumValuesOptional.get();
                        if (!enumValues.contains(convertedValue)) {
                            throw new IllegalArgumentException(
                                    "Invalid value for ENUM: " + convertedValue +
                                    " (allowed values: " + enumValues + ")");
                        }
                    }
                }
            }
            result.put(columnName, convertedValue);
        }
        return result;
    }

    public static ImmutableMap<Integer, Object> toByColumnPoisitionedImmutableMap(
            Table table, Map<String, Object> columnValues) {
        NamedResourceStore<Column> columns = table.columns();
        ImmutableList<String> columnNames = columns.names();
        return ImmutableMap.fromMap(columnValues).map(columnNames::indexOf, v -> v);
    }

    public static Optional<Column> getAutoIncrementedColumn(Table table) {
        for (Column column : table.columns().resources()) {
            if (column.definition().isAutoIncremented()) {
                return Optional.of(column);
            }
        }
        
        return Optional.empty();
    }
    
    public static List<LargeInteger> findAllNonNull(Table table, String columnName, Object value) {
        List<LargeInteger> result = new ArrayList<>();
        for (TableIndex tableIndex : table.indexes().resources()) {
            if (tableIndex.columnNames().get(0).equals(columnName)) {
                tableIndex.find(value).forEach(result::add);
                return result;
            }
        }
        
        @SuppressWarnings("unchecked")
        Comparator<Object> comparator = (Comparator<Object>) table.columns().get(columnName).definition().comparator();
        LargeInteger size = table.size();
        for (LargeInteger i = LargeInteger.ZERO; i.isLessThan(size); i = i.add(LargeInteger.ONE)) {
            Row row = table.row(i);
            Object foundValue = row.get(columnName);
            if (foundValue != null && comparator.compare(value, foundValue) == 0) {
                result.add(i);
            }
        }
        
        return result;
    }

    private static <T> List<T> collectIterator(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
    
    public static MultiComparator createMultiComparator(
            List<OrderByEntry> orderByEntries, Function<String, Table> tableResolver) {
        MultiComparatorBuilder builder = MultiComparator.builder();
        for (OrderByEntry orderByEntry : orderByEntries) {
            String columnName = orderByEntry.fieldName;
            Table table = tableResolver.apply(orderByEntry.tableAlias);
            ColumnDefinition columnDefinition = table.columns().get(columnName).definition();
            Comparator<?> columnComparator = columnDefinition.comparator();
            boolean nullsLow = isNullsLow(orderByEntry);
            builder.add(columnComparator, true, orderByEntry.ascOrder, nullsLow);
        }
        return builder.build();
    }
    
    public static boolean isNullsLow(OrderByEntry orderByEntry) {
        if (orderByEntry.nullsOrderMode == NullsOrderMode.NULLS_AUTO) {
            return true;
        } else {
            boolean nullsFirst = (orderByEntry.nullsOrderMode == NullsOrderMode.NULLS_FIRST);
            return (orderByEntry.ascOrder == nullsFirst);
        }
    }

    public static ImmutableList<Object> extractOrderValues(
            List<OrderByEntry> orderByEntries,
            Function<String, Table> tableResolver,
            Function<String, LargeInteger> rowIndexResolver) {
        List<Object> result = new ArrayList<>(orderByEntries.size());
        Map<String, Row> rowCache = new HashMap<>();
        for (OrderByEntry orderByEntry : orderByEntries) {
            LargeInteger rowIndex = rowIndexResolver.apply(orderByEntry.tableAlias);
            if (rowIndex != null) {
                Table table = tableResolver.apply(orderByEntry.tableAlias);
                Row row = rowCache.computeIfAbsent(orderByEntry.tableAlias, a -> table.row(rowIndex));
                result.add(row.get(orderByEntry.fieldName));
            } else {
                result.add(null);
            }
        }
        return ImmutableList.fromCollection(result);
    }
    
}
