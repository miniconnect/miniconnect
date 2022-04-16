package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleRow;
import hu.webarticum.miniconnect.rdmsframework.util.IndexUtil;
import hu.webarticum.miniconnect.util.ChainedIterator;
import hu.webarticum.miniconnect.util.FilteringIterator;
import hu.webarticum.miniconnect.util.IteratorAdapter;

public class DiffTable implements Table {
    
    private final Table baseTable;
    
    private final DiffTableIndexStore indexStore;
    
    private final List<ImmutableList<Object>> insertedRows = new ArrayList<>();
    
    private final NavigableMap<BigInteger, ImmutableMap<Integer, Object>> updates = new TreeMap<>();
    
    private final NavigableSet<BigInteger> deletions = new TreeSet<>();

    
    public DiffTable(Table baseTable) {
        this.baseTable = baseTable;
        this.indexStore = new DiffTableIndexStore();
    }


    @Override
    public String name() {
        return baseTable.name();
    }

    @Override
    public NamedResourceStore<Column> columns() {
        return baseTable.columns();
    }

    @Override
    public NamedResourceStore<TableIndex> indexes() {
        return indexStore;
    }

    @Override
    public BigInteger size() {
        return baseTable.size()
                .add(BigInteger.valueOf(insertedRows.size()))
                .subtract(BigInteger.valueOf(deletions.size()));
    }

    @Override
    public synchronized Row row(BigInteger rowIndex) {
        BigInteger baseTableSize = baseTable.size();
        BigInteger adjustedRowIndex = adjustByDeletions(BigInteger.ZERO, rowIndex);
        
        if (adjustedRowIndex.compareTo(baseTableSize) >= 0) {
            ImmutableList<Object> rowData =
                    insertedRows.get(adjustedRowIndex.subtract(baseTableSize).intValueExact());
            return new SimpleRow(baseTable.columns().names(), rowData);
        }
        
        Row originalRow = baseTable.row(adjustedRowIndex);
        ImmutableMap<Integer, Object> rowUpdates = updates.get(adjustedRowIndex);
        if (rowUpdates == null) {
            return originalRow;
        }
        
        return new UpdatedRow(originalRow, rowUpdates);
    }
    
    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void applyPatch(TablePatch patch) {
        insertedRows.addAll(patch.insertedRows());
        applyUpdates(patch.updates());
        applyDeletions(patch.deletions());
    }
    
    private void applyUpdates(
            NavigableMap<BigInteger, ImmutableMap<Integer, Object>> patchUpdates) {
        BigInteger baseTableSize = baseTable.size();
        BigInteger internalPosition = BigInteger.ZERO;
        BigInteger viewPosition = BigInteger.ZERO;
        for (Map.Entry<BigInteger, ImmutableMap<Integer, Object>> entry :
                patchUpdates.entrySet()) {
            BigInteger rowIndex = entry.getKey();
            ImmutableMap<Integer, Object> rowUpdates = entry.getValue();
            BigInteger remainingCount = rowIndex.subtract(viewPosition);
            BigInteger adjustedRowIndex = adjustByDeletions(internalPosition, remainingCount);

            applyUpdate(adjustedRowIndex, rowUpdates, baseTableSize);
            
            internalPosition = adjustedRowIndex.add(BigInteger.ONE);
            viewPosition = rowIndex.add(BigInteger.ONE);
        }
    }

    private void applyUpdate(
            BigInteger adjustedRowIndex,
            ImmutableMap<Integer, Object> rowUpdates,
            BigInteger baseTableSize) {
        if (adjustedRowIndex.compareTo(baseTableSize) < 0) {
            ImmutableMap<Integer, Object> currentRowUpdates = updates.get(adjustedRowIndex);
            ImmutableMap<Integer, Object> newRowUpdates;
            if (currentRowUpdates == null) {
                newRowUpdates = rowUpdates;
            } else {
                newRowUpdates = currentRowUpdates.merge(rowUpdates);
            }
            updates.put(adjustedRowIndex, newRowUpdates);
        } else {
            int insertIndex = adjustedRowIndex.subtract(baseTableSize).intValueExact();
            ImmutableList<Object> currentRow = insertedRows.get(insertIndex);
            ImmutableList<Object> updatedRow = currentRow.mapIndex(rowUpdates::getOrDefault);
            insertedRows.set(insertIndex, updatedRow);
        }
    }
    
    private void applyDeletions(NavigableSet<BigInteger> patchDeletions) {
        BigInteger baseTableSize = baseTable.size();
        BigInteger currentDeletionCount = BigInteger.valueOf(deletions.size());
        BigInteger reducedSize = baseTableSize.subtract(currentDeletionCount);
        applyInnerDeletions(patchDeletions.headSet(reducedSize));
        applyOuterDeletions(patchDeletions.tailSet(reducedSize, true), reducedSize);
    }
    
    private void applyInnerDeletions(SortedSet<BigInteger> innerDeletions) {
        BigInteger internalPosition = BigInteger.ZERO;
        BigInteger viewPosition = BigInteger.ZERO;
        for (BigInteger rowIndex : innerDeletions) {
            BigInteger remainingCount = rowIndex.subtract(viewPosition);
            BigInteger adjustedRowIndex = adjustByDeletions(internalPosition, remainingCount);

            deletions.add(adjustedRowIndex);
            updates.remove(adjustedRowIndex);
            
            internalPosition = adjustedRowIndex.add(BigInteger.ONE);
            viewPosition = rowIndex.add(BigInteger.ONE);
        }
    }

    private void applyOuterDeletions(
            NavigableSet<BigInteger> outerDeletions, BigInteger reducedSize) {
        Iterator<BigInteger> descIterator = outerDeletions.descendingIterator();
        while (descIterator.hasNext()) {
            BigInteger outerIndex = descIterator.next();
            int insertionIndex = outerIndex.subtract(reducedSize).intValueExact();
            insertedRows.remove(insertionIndex);
        }
    }

    private BigInteger adjustByDeletions(BigInteger start, BigInteger count) {
        BigInteger targetPosition;
        
        BigInteger position = start;
        BigInteger remaining = count;
        do {
            targetPosition = position.add(remaining);
            Set<BigInteger> foundItems = deletions.subSet(position, targetPosition);
            remaining = BigInteger.valueOf(foundItems.size());
            position = targetPosition;
        } while (!remaining.equals(BigInteger.ZERO));
        
        while (deletions.contains(targetPosition)) {
            targetPosition = targetPosition.add(BigInteger.ONE);
        }
        
        return targetPosition;
    }

    private BigInteger deadjustByDeletions(BigInteger baseRowIndex) {
        Collection<BigInteger> subDeletions = deletions.subSet(BigInteger.ZERO, baseRowIndex);
        BigInteger deletionCount = BigInteger.valueOf(subDeletions.size());
        return baseRowIndex.subtract(deletionCount);
    }
    
    
    private static class UpdatedRow implements Row {
        
        private final Row baseRow;
        
        private final ImmutableMap<Integer, Object> updates;
        
        
        private UpdatedRow(Row baseRow, ImmutableMap<Integer, Object> updates) {
            this.baseRow = baseRow;
            this.updates = updates;
        }


        @Override
        public ImmutableList<String> columnNames() {
            return baseRow.columnNames();
        }

        @Override
        public Object get(int columnPosition) {
            Object updatedValue = updates.get(columnPosition);
            return updatedValue != null ? updatedValue : baseRow.get(columnPosition);
        }

        @Override
        public Object get(String columnName) {
            int columnPosition = baseRow.columnNames().indexOf(columnName);
            return get(columnPosition);
        }

        @Override
        public ImmutableList<Object> getAll() {
            List<Object> resultBuilder = new ArrayList<>();
            int width = baseRow.columnNames().size();
            for (int i = 0; i < width; i++) {
                resultBuilder.add(get(i));
            }
            return ImmutableList.fromCollection(resultBuilder);
        }

        @Override
        public ImmutableMap<String, Object> getMap() {
            return getMap(baseRow.columnNames());
        }

        @Override
        public ImmutableMap<String, Object> getMap(ImmutableList<String> columnNames) {
            Map<String, Object> resultBuilder = new HashMap<>();
            for (String columnName : columnNames) {
                resultBuilder.put(columnName, get(columnName));
            }
            return ImmutableMap.fromMap(resultBuilder);
        }
        
    }
    
    
    private class DiffTableIndexStore implements NamedResourceStore<TableIndex> {
        
        private final NamedResourceStore<TableIndex> baseStore = baseTable.indexes();
        

        @Override
        public ImmutableList<String> names() {
            return baseStore.names();
        }

        @Override
        public ImmutableList<TableIndex> resources() {
            return names().map(this::get);
        }

        @Override
        public boolean contains(String name) {
            return baseStore.contains(name);
        }

        @Override
        public TableIndex get(String name) {
            TableIndex baseIndex = baseStore.get(name);
            if (insertedRows.isEmpty() && updates.isEmpty() && deletions.isEmpty()) {
                return baseIndex;
            } else {
                return new DiffTableIndex(baseIndex);
            }
        }
        
    }
    
    
    private class DiffTableIndex implements TableIndex {
        
        private final TableIndex baseIndex;
        
        private final Set<BigInteger> updatedRowIndexes;
        
        private final ArrayList<DiffTableIndexEntry> indexEntries;
        

        public DiffTableIndex(TableIndex baseIndex) {
            
            System.out.println("insertedRows: " + insertedRows);
            System.out.println("updates: " + updates);
            System.out.println("deletions: " + deletions);
            
            this.baseIndex = baseIndex;
            
            ImmutableList<String> tableColumnNames = baseTable.columns().names();
            ImmutableList<Integer> columnIndexes =
                    baseIndex.columnNames().map(tableColumnNames::indexOf);
            
            int fullUpdateCount = updates.size() + insertedRows.size();
            this.updatedRowIndexes = new HashSet<>(fullUpdateCount);
            this.indexEntries = new ArrayList<>(fullUpdateCount);
            
            BigInteger position = BigInteger.ZERO;
            BigInteger fullDeletionCount = BigInteger.ZERO;
            for (Map.Entry<BigInteger, ImmutableMap<Integer, Object>> entry : updates.entrySet()) {
                BigInteger baseRowIndex = entry.getKey();
                
                System.out.println("position: " + position +  ", baseRowIndex: " + baseRowIndex);
                
                Collection<BigInteger> subDeletions = deletions.subSet(position, baseRowIndex);
                BigInteger subDeletionCount = BigInteger.valueOf(subDeletions.size());
                fullDeletionCount = fullDeletionCount.add(subDeletionCount);
                BigInteger rowIndex = baseRowIndex.subtract(fullDeletionCount);

                ImmutableMap<Integer, Object> rowUpdates = entry.getValue();
                boolean updated = false;
                for (Integer columnIndex : columnIndexes) {
                    if (rowUpdates.containsKey(columnIndex)) {
                        updated = true;
                        break;
                    }
                }
                
                if (updated) {
                    this.updatedRowIndexes.add(rowIndex);
                    Row baseRow = baseTable.row(baseRowIndex);
                    Row updatedRow = new UpdatedRow(baseRow, rowUpdates);
                    ImmutableList<Object> updatedData = columnIndexes.map(updatedRow::get);
                    DiffTableIndexEntry indexEntry = new DiffTableIndexEntry(rowIndex, updatedData);
                    this.indexEntries.add(indexEntry);
                }
                
                position = baseRowIndex.add(BigInteger.ONE);
            }
            Collection<BigInteger> tailDeletions = deletions.tailSet(position);
            BigInteger tailDeletionCount = BigInteger.valueOf(tailDeletions.size());
            fullDeletionCount = fullDeletionCount.add(tailDeletionCount);
            BigInteger innerSize = baseTable.size().subtract(fullDeletionCount);
            
            int insertionCount = insertedRows.size();
            for (int i = 0; i < insertionCount; i++) {
                ImmutableList<Object> insertedRow = insertedRows.get(i);
                ImmutableList<Object> insertedData = columnIndexes.map(insertedRow::get);
                BigInteger rowIndex = BigInteger.valueOf(i).add(innerSize);
                DiffTableIndexEntry indexEntry = new DiffTableIndexEntry(rowIndex, insertedData);
                this.updatedRowIndexes.add(rowIndex);
                this.indexEntries.add(indexEntry);
            }
            
            this.indexEntries.trimToSize();
        }
        
        
        @Override
        public String name() {
            return baseIndex.name();
        }

        @Override
        public boolean isUnique() {
            
            // TODO FIXME
            return false;
            
        }

        @Override
        public ImmutableList<String> columnNames() {
            return baseIndex.columnNames();
        }

        @Override
        public TableSelection findMulti(
                ImmutableList<?> from,
                InclusionMode fromInclusionMode,
                ImmutableList<?> to,
                InclusionMode toInclusionMode,
                ImmutableList<NullsMode> nullsModes,
                ImmutableList<SortMode> sortModes) {
            boolean sort = !sortModes.isEmpty() && sortModes.get(0).isSorted();
            
            TableSelection baseSelection = baseIndex.findMulti(
                    from, fromInclusionMode, to, toInclusionMode, nullsModes, sortModes);
            MultiComparator multiComparator = IndexUtil.createMultiComparator(
                    baseTable, baseIndex.columnNames(), sortModes);
            Predicate<ImmutableList<Object>> predicate = IndexUtil.createPredicate(
                    from, fromInclusionMode, to, toInclusionMode, multiComparator);
            if (sort) {
                return new SortedDiffTableSelection(
                        baseSelection,
                        predicate,
                        multiComparator,
                        from,
                        fromInclusionMode,
                        to,
                        toInclusionMode,
                        nullsModes,
                        sortModes);
            } else {
                return new UnsortedDiffTableSelection(baseSelection, predicate);
            }
        }
    

        private abstract class AbstractDiffTableSelection implements TableSelection {
    
            protected final TableSelection baseSelection;
    
            protected final Set<BigInteger> filteredUpdatedRowIndexes;
    
            protected final ArrayList<DiffTableIndexEntry> filteredIndexEntries;
            
            
            protected AbstractDiffTableSelection(
                    TableSelection baseSelection,
                    Predicate<ImmutableList<Object>> predicate) {
                this.baseSelection = baseSelection;
                this.filteredUpdatedRowIndexes = new HashSet<>();
                this.filteredIndexEntries = new ArrayList<>(indexEntries.size());
                for (DiffTableIndexEntry indexEntry : indexEntries) {
                    if (predicate.test(indexEntry.values)) {
                        this.filteredUpdatedRowIndexes.add(indexEntry.rowIndex);
                        this.filteredIndexEntries.add(indexEntry);
                    }
                }
                this.filteredIndexEntries.trimToSize();
            }
            
            
            @Override
            public boolean containsRow(BigInteger rowIndex) {
                if (updatedRowIndexes.contains(rowIndex)) {
                    return filteredUpdatedRowIndexes.contains(rowIndex);
                } else {
                    BigInteger adjustedRowIndex = adjustByDeletions(BigInteger.ZERO, rowIndex);
                    return baseSelection.containsRow(adjustedRowIndex);
                }
            }

            protected Iterator<BigInteger> wrapIterator(Iterator<BigInteger> baseIterator) {
                return new FilteringIterator<>(
                        new IteratorAdapter<>(
                                new FilteringIterator<>(
                                        baseIterator,
                                        v -> !deletions.contains(v)),
                                DiffTable.this::deadjustByDeletions),
                        v -> !updatedRowIndexes.contains(v));
            }
    
        }
        
    
        private class SortedDiffTableSelection extends AbstractDiffTableSelection {
            
            private final ImmutableList<?> from;
            
            private final InclusionMode fromInclusionMode;
            
            private final ImmutableList<?> to;
            
            private final InclusionMode toInclusionMode;
            
            private final ImmutableList<NullsMode> nullsModes;
            
            private final ImmutableList<SortMode> sortModes;
            
    
            public SortedDiffTableSelection( // NOSONAR currently these many parameters are OK
                    TableSelection baseSelection,
                    Predicate<ImmutableList<Object>> predicate,
                    MultiComparator multiComparator,
                    ImmutableList<?> from,
                    InclusionMode fromInclusionMode,
                    ImmutableList<?> to,
                    InclusionMode toInclusionMode,
                    ImmutableList<NullsMode> nullsModes,
                    ImmutableList<SortMode> sortModes) {
                super(baseSelection, predicate);
                this.filteredIndexEntries.sort(
                        (e1, e2) -> multiComparator.compare(e1.values, e2.values));

                this.from = from;
                this.fromInclusionMode = fromInclusionMode;
                this.to = to;
                this.toInclusionMode = toInclusionMode;
                this.nullsModes = nullsModes;
                this.sortModes = sortModes;
            }
            
            
            @Override
            public Iterator<BigInteger> iterator() {
                if (filteredIndexEntries.isEmpty()) {
                    return wrapIterator(baseSelection.iterator());
                }

                List<Iterator<BigInteger>> iterators = new LinkedList<>();
                
                DiffTableIndexEntry firstEntry = filteredIndexEntries.get(0);
                
                TableSelection leadingBaseSelection = baseIndex.findMulti(
                        from,
                        fromInclusionMode,
                        firstEntry.values,
                        InclusionMode.INCLUDE,
                        nullsModes,
                        sortModes);
                iterators.add(wrapIterator(leadingBaseSelection.iterator()));
                
                iterators.add(createMiddleIterator());

                DiffTableIndexEntry lastEntry = filteredIndexEntries.get(
                        filteredIndexEntries.size() - 1);
                iterators.add(Collections.singleton(lastEntry.rowIndex).iterator());
                
                TableSelection trailingBaseSelection = baseIndex.findMulti(
                        lastEntry.values,
                        InclusionMode.EXCLUDE,
                        to,
                        toInclusionMode,
                        nullsModes,
                        sortModes);
                iterators.add(wrapIterator(trailingBaseSelection.iterator()));
                
                return ChainedIterator.allOf(iterators);
            }

            private Iterator<BigInteger> createMiddleIterator() {
                int entryCount = filteredIndexEntries.size();
                return ChainedIterator.over(new IteratorAdapter<>(
                        IntStream.range(0, entryCount - 1).iterator(),
                        i -> {
                            DiffTableIndexEntry beforeEntry = filteredIndexEntries.get(i);
                            DiffTableIndexEntry afterEntry = filteredIndexEntries.get(i + 1);
                            TableSelection betweenBaseSelection = baseIndex.findMulti(
                                    beforeEntry.values,
                                    InclusionMode.EXCLUDE,
                                    afterEntry.values,
                                    InclusionMode.INCLUDE,
                                    nullsModes,
                                    sortModes);
                            return ChainedIterator.of(
                                    Collections.singleton(beforeEntry.rowIndex).iterator(),
                                    wrapIterator(betweenBaseSelection.iterator()));
                        }));
            }
            
        }
        
        
        private class UnsortedDiffTableSelection extends AbstractDiffTableSelection {
    
            public UnsortedDiffTableSelection(
                    TableSelection baseSelection,
                    Predicate<ImmutableList<Object>> predicate) {
                super(baseSelection, predicate);
            }
            
            
            @Override
            public Iterator<BigInteger> iterator() {
                return ChainedIterator.of(
                        wrapIterator(baseSelection.iterator()),
                        new IteratorAdapter<>(filteredIndexEntries.iterator(), e -> e.rowIndex));
            }
    
        }
        
    }
    
    
    private static class DiffTableIndexEntry {
        
        private final BigInteger rowIndex;
        
        private final ImmutableList<Object> values;
        
        
        private DiffTableIndexEntry(BigInteger rowIndex, ImmutableList<Object> values) {
            this.rowIndex = rowIndex;
            this.values = values;
        }
        
    }

}
