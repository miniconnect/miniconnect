package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleRow;

public class DiffTable implements Table {
    
    private final Table baseTable;
    
    private final List<ImmutableList<Object>> insertedRows = new ArrayList<>();
    
    private final Map<BigInteger, ImmutableMap<Integer, Object>> updates = new HashMap<>();
    
    private final NavigableSet<BigInteger> deletions = new TreeSet<>();

    
    public DiffTable(Table baseTable) {
        this.baseTable = baseTable;
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
        
        // TODO
        return null;
        
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
    
}
