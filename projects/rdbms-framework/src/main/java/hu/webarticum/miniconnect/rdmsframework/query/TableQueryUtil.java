package hu.webarticum.miniconnect.rdmsframework.query;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.RangeSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.record.converter.DefaultConverter;

public class TableQueryUtil {
    
    private static final DefaultConverter CONVERTER = new DefaultConverter();
    

    private TableQueryUtil() {
        // utility class
    }


    public static void checkFields(Table table, Iterable<String> columnNames) {
        columnNames.forEach(n -> checkField(table, n));
    }

    public static void checkField(Table table, String columnName) {
        if (!table.columns().contains(columnName)) {
            throw new IllegalArgumentException(String.format(
                    "No column '%s' in table '%s'", columnName, table.name()));
        }
    }

    public static List<BigInteger> filterRows(Table table, Map<String, Object> queryWhere) {
        Map<ImmutableList<String>, TableIndex> indexesByColumnName = new LinkedHashMap<>();
        Set<String> unindexedColumnNames =
                collectIndexes(table, queryWhere.keySet(), indexesByColumnName);

        TableSelection firstSelection = null;
        List<TableSelection> moreSelections = new ArrayList<>();
        for (Map.Entry<ImmutableList<String>, TableIndex> entry : indexesByColumnName.entrySet()) {
            ImmutableList<String> columnNames = entry.getKey();
            TableIndex tableIndex = entry.getValue();
            ImmutableList<Object> values = columnNames.map(queryWhere::get);
            TableSelection selection = tableIndex.findMulti(values);
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

    private static List<BigInteger> matchRows(
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
                Object expectedValue = queryWhere.get(columnName);
                Object actualValue = table.row(rowIndex).get(columnName);
                if (!Objects.equals(actualValue, expectedValue)) {
                    return false;
                }
            }
        }
        
        return true;
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
    
    public static Map<String, Object> convertColumnValues(
            Table table, Map<String, Object> columnValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        NamedResourceStore<Column> columns = table.columns();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            Class<?> columnClazz = columns.get(columnName).definition().clazz();
            Object convertedValue = CONVERTER.convert(value, columnClazz);
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
    
}
