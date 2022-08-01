package hu.webarticum.miniconnect.rdmsframework.query;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.RangeSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.SortMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.record.converter.DefaultConverter;

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
            throw new IllegalArgumentException(String.format("No column '%s' in table '%s'", columnName, table.name()));
        }
    }

    public static List<BigInteger> filterRows(Table table, Map<String, Object> queryWhere, Integer unorderedLimit) {
        Map<ImmutableList<String>, TableIndex> indexesByColumnName = new LinkedHashMap<>();
        Set<String> unindexedColumnNames = collectIndexes(table, queryWhere.keySet(), indexesByColumnName);

        TableSelection firstSelection = null;
        List<TableSelection> moreSelections = new ArrayList<>();
        for (Map.Entry<ImmutableList<String>, TableIndex> entry : indexesByColumnName.entrySet()) {
            ImmutableList<String> columnNames = entry.getKey();
            TableIndex tableIndex = entry.getValue();
            ImmutableList<Object> values = columnNames.map(queryWhere::get);
            TableSelection selection = tableIndex.findMulti(
                    values.map(v -> v instanceof SpecialCondition ? null : v),
                    InclusionMode.INCLUDE,
                    values.map(v -> v instanceof SpecialCondition ? null : v),
                    InclusionMode.INCLUDE,
                    values.map(TableQueryUtil::nullsModeForValue),
                    values.map(v -> SortMode.UNSORTED));
            if (firstSelection == null) {
                firstSelection = selection;
            } else {
                moreSelections.add(selection);
            }
        }
        if (firstSelection == null) {
            firstSelection = new RangeSelection(BigInteger.valueOf(0L), table.size());
        }
        
        return matchRows(table, queryWhere, firstSelection, moreSelections, unindexedColumnNames, unorderedLimit);
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
    
    private static List<BigInteger> matchRows(
            Table table, 
            Map<String, Object> queryWhere,
            TableSelection firstSelection,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames,
            Integer unorderedLimit) {
        if (unorderedLimit == null) {
            return matchRowsUnlimited(table, queryWhere, firstSelection, moreSelections, unindexedColumnNames);
        } else {
            return matchRowsLimited(
                    table, queryWhere, firstSelection, moreSelections, unindexedColumnNames, unorderedLimit);
        }
    }
    
    private static List<BigInteger> matchRowsUnlimited(
            Table table, 
            Map<String, Object> queryWhere,
            TableSelection firstSelection,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames) {
        List<BigInteger> result = new ArrayList<>();
        for (BigInteger rowIndex : firstSelection) {
            if (isRowMatchingWithMore(
                    table, rowIndex, queryWhere, moreSelections, unindexedColumnNames)) {
                result.add(rowIndex);
            }
        }
        return result;
    }

    private static List<BigInteger> matchRowsLimited(
            Table table, 
            Map<String, Object> queryWhere,
            TableSelection firstSelection,
            List<TableSelection> moreSelections,
            Set<String> unindexedColumnNames,
            int unorderedLimit) {
        List<BigInteger> result = new ArrayList<>();
        int remaining = unorderedLimit;
        for (BigInteger rowIndex : firstSelection) {
            if (isRowMatchingWithMore(
                    table, rowIndex, queryWhere, moreSelections, unindexedColumnNames)) {
                result.add(rowIndex);
                remaining--;
                if (remaining == 0) {
                    break;
                }
            }
        }
        return result;
    }
    
    private static boolean isRowMatchingWithMore(
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

    public static Set<String> collectIndexes(
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
    
    public static Map<String, Object> convertColumnValues(Table table, Map<String, Object> columnValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        NamedResourceStore<Column> columns = table.columns();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            Object convertedValue = value;
            if (!(value instanceof SpecialCondition)) {
                Class<?> columnClazz = columns.get(columnName).definition().clazz();
                convertedValue = convert(value, columnClazz);
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
    
}
