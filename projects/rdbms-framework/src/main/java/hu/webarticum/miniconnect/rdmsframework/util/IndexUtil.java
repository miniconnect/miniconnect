package hu.webarticum.miniconnect.rdmsframework.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.SortMode;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator.MultiComparatorBuilder;

public final class IndexUtil {

    private IndexUtil() {
        // utility class
    }
    

    public static MultiComparator createMultiComparator(
            Table table,
            ImmutableList<String> columnNames,
            ImmutableList<SortMode> sortModes) {
        int size = columnNames.size();
        ImmutableList<SortMode> extendedSortModes = sortModes.resize(size, i -> SortMode.UNSORTED);
        NamedResourceStore<Column> columns = table.columns();
        MultiComparatorBuilder builder = MultiComparator.builder();
        for (int i = 0; i < size; i++) {
            String columnName = columnNames.get(i);
            SortMode sortMode = extendedSortModes.get(i);
            ColumnDefinition columnDefinition = columns.get(columnName).definition();
            Comparator<?> columnComparator = columnDefinition.comparator();
            boolean nullable = columnDefinition.isNullable();
            builder.add(columnComparator, nullable, sortMode.isAsc(), sortMode.isNullsFirst());
        }
        return builder.build();
    }
    
    // TODO: now this is overcomplicated, move it to its own class that implements Predicate<ImmutableList<Object>>
    public static Predicate<ImmutableList<Object>> createPredicate(
            ImmutableList<?> from,
            InclusionMode fromInclusionMode,
            ImmutableList<?> to,
            InclusionMode toInclusionMode,
            ImmutableList<NullsMode> nullsModes,
            MultiComparator multiComparator) {
        Predicate<ImmutableList<Object>> result = null;
        
        Map<Integer, NullsMode> specialNullsModeMap = new HashMap<>(nullsModes.size());
        {
            int i = 0;
            for (NullsMode nullsMode : nullsModes) {
                if (nullsMode != NullsMode.WITH_NULLS) {
                    specialNullsModeMap.put(i, nullsMode);
                }
                i++;
            }
        }
        if (!specialNullsModeMap.isEmpty()) {
            result = v -> {
                for (Map.Entry<Integer, NullsMode> entry : specialNullsModeMap.entrySet()) {
                    int i = entry.getKey();
                    NullsMode nullsMode = entry.getValue();
                    Object value = v.get(i);
                    if (nullsMode == NullsMode.NO_NULLS) {
                        if (value == null) {
                            return false;
                        }
                    } else if (nullsMode == NullsMode.NULLS_ONLY) {
                        if (value != null) {
                            return false;
                        }
                    }
                }
                return true;
            };
        }
        
        if (from != null) {
            @SuppressWarnings("unchecked")
            ImmutableList<Object> fromAsObjects = (ImmutableList<Object>) from;
            Predicate<ImmutableList<Object>> fromPredicate;
            if (fromInclusionMode == InclusionMode.INCLUDE) {
                fromPredicate = v -> multiComparator.compare(v, fromAsObjects) >= 0;
            } else {
                fromPredicate = v -> multiComparator.compare(v, fromAsObjects) > 0;
            }
            result = result != null ? result.and(fromPredicate) : fromPredicate;
        }
        
        if (to != null) {
            @SuppressWarnings("unchecked")
            ImmutableList<Object> toAsObjects = (ImmutableList<Object>) to;
            Predicate<ImmutableList<Object>> toPredicate;
            if (toInclusionMode == InclusionMode.INCLUDE) {
                toPredicate = v -> multiComparator.compare(v, toAsObjects) <= 0;
            } else {
                toPredicate = v -> multiComparator.compare(v, toAsObjects) < 0;
            }
            result = result != null ? result.and(toPredicate) : toPredicate;
        }
        
        if (result == null) {
            result = v -> true;
        }
        
        return result;
    }

}
