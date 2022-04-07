package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Column;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;

public class DiffTable implements Table {
    
    private final Table baseTable;
    
    private final ArrayList<Row> insertedRows = new ArrayList<>();
    
    private final Map<BigInteger, Map<Integer, Object>> updates = new HashMap<>();
    
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
        
        // TODO
        return null;
        
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
        BigInteger adjustedRowIndex = adjustByDeletions(rowIndex);
        
        if (adjustedRowIndex.compareTo(baseTableSize) > 0) {
            return insertedRows.get(adjustedRowIndex.subtract(baseTableSize).intValueExact());
        }
        
        Row originalRow = baseTable.row(adjustedRowIndex);
        Map<Integer, Object> rowUpdates = updates.get(adjustedRowIndex);
        if (rowUpdates == null) {
            return originalRow;
        }
        
        return new UpdatedRow(originalRow, rowUpdates);
    }
    
    private BigInteger adjustByDeletions(BigInteger rowIndex) {
        BigInteger targetPosition;
        
        BigInteger position = BigInteger.ZERO;
        BigInteger remaining = rowIndex;
        while (true) {
            targetPosition = position.add(remaining);
            Set<BigInteger> foundItems = deletions.subSet(position, targetPosition);
            BigInteger foundCount = BigInteger.valueOf(foundItems.size());
            if (foundCount.equals(BigInteger.ZERO)) {
                break;
            }
        }
        
        while (deletions.contains(targetPosition)) {
            targetPosition = targetPosition.add(BigInteger.ONE);
        }
        
        return targetPosition;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void applyPatch(TablePatch patch) {
        
        // TODO

        
    }
    
    
    private static class UpdatedRow implements Row {
        
        private final Row baseRow;
        
        // FIXME: something more immutable?
        private final Map<Integer, Object> updates;
        
        
        private UpdatedRow(Row baseRow, Map<Integer, Object> updates) {
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
