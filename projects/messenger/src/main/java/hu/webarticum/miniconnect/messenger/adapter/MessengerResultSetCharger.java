package hu.webarticum.miniconnect.messenger.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.messenger.chargeable.ChargeableContentAccess;
import hu.webarticum.miniconnect.messenger.chargeable.FileChargeableContentAccess;
import hu.webarticum.miniconnect.messenger.chargeable.MemoryChargeableContentAccess;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.messenger.util.ExceptionUtil;
import hu.webarticum.miniconnect.tool.result.StoredContentAccess;

public class MessengerResultSetCharger {
    
    private static final int MAX_MEMORY_CONTENT_SIZE = 10 * 1024 * 1024;
    

    private final MessengerResultSet resultSet;
    
    private final Consumer<Response> consumerReference;
    
    private final Map<Long, Map<Integer, List<ResultSetValuePartResponse>>> unhandledParts =
            new HashMap<>();
    
    private final Map<CellPosition, ChargeableContentAccess> chargeables = new HashMap<>();

    
    public MessengerResultSetCharger(
            ResultResponse resultResponse,
            Consumer<Response> consumerReference) {
        this.resultSet = new MessengerResultSet(
                resultResponse.columnHeaders().map(ColumnHeaderData::toMiniColumnHeader));
        this.consumerReference = consumerReference;
    }

    
    public MiniResultSet resultSet() {
        return resultSet;
    }
    
    public synchronized void accept(ResultSetRowsResponse rowsResponse) {
        long rowIndex = rowsResponse.rowOffset();
        for (ImmutableList<CellData> rowData : rowsResponse.rows()) {
            acceptRow(rowIndex, rowData);
            rowIndex++;
        }
        new Blackhole().consume(consumerReference);
    }
    
    private void acceptRow(long rowIndex, ImmutableList<CellData> rowData) {
        ImmutableList<MiniValue> row = rowData.mapIndex(
                (columnIndex, cellData) -> createValue(rowIndex, columnIndex, cellData));
        unhandledParts.remove(rowIndex);
        
        try {
            resultSet.rowQueue.put(row);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private MiniValue createValue(long rowIndex, int columnIndex, CellData cellData) {
        long fullLength = cellData.fullLength();
        ByteString content = cellData.content();
        
        MiniContentAccess contentAccess;
        if (fullLength == content.length()) {
            contentAccess = new StoredContentAccess(content);
        } else {
            ChargeableContentAccess chargeable = createChargeable(fullLength);
            chargeable.accept(0L, content);
            processUnhandledParts(rowIndex, columnIndex, chargeable);
            chargeables.put(new CellPosition(rowIndex, columnIndex), chargeable);
            contentAccess = chargeable;
        }
        
        MiniValueDefinition valueDefinition =
                resultSet.columnHeaders.get(columnIndex).valueDefinition();
        
        return new MessengerValue(valueDefinition, cellData.isNull(), contentAccess);
    }
    
    private void processUnhandledParts(
            long rowIndex, int columnIndex, ChargeableContentAccess chargeable) {
        
        Map<Integer, List<ResultSetValuePartResponse>> unhandledRow = unhandledParts.get(rowIndex);
        if (unhandledRow == null) {
            return;
        }
        
        List<ResultSetValuePartResponse> unhandledCell = unhandledRow.get(columnIndex);
        if (unhandledCell == null) {
            return;
        }

        for (ResultSetValuePartResponse partResponse : unhandledCell) {
            chargeable.accept(partResponse.offset(), partResponse.content());
        }
    }

    private ChargeableContentAccess createChargeable(long length) {
        if (length > MAX_MEMORY_CONTENT_SIZE) {
            return new FileChargeableContentAccess(length);
        } else {
            return new MemoryChargeableContentAccess((int) length);
        }
    }

    public synchronized void acceptPart(ResultSetValuePartResponse partResponse) {
        long rowIndex = partResponse.rowIndex();
        int columnIndex = partResponse.columnIndex();
        CellPosition cellPosition = new CellPosition(rowIndex, columnIndex);
        
        ChargeableContentAccess chargeable = chargeables.get(cellPosition);
        if (chargeable != null) {
            chargeable.accept(partResponse.offset(), partResponse.content());
            if (chargeable.isCompleted()) {
                chargeables.remove(cellPosition);
            }
        } else if (rowIndex > resultSet.currentRowIndex) {
            storeUnhandledPart(partResponse);
        } else {
            // ignored
        }
    }
    
    private void storeUnhandledPart(ResultSetValuePartResponse partResponse) {
        Map<Integer, List<ResultSetValuePartResponse>> unhandledRow =
                unhandledParts.computeIfAbsent(
                        partResponse.rowIndex(),
                        k -> new HashMap<>());
        List<ResultSetValuePartResponse> unhandledCell =
                unhandledRow.computeIfAbsent(
                        partResponse.columnIndex(),
                        k -> new ArrayList<>());
        unhandledCell.add(partResponse);
    }
    
    public void acceptEof() {
        try {
            resultSet.rowQueue.put(MessengerResultSet.eofRow());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
    private static class CellPosition {
        
        final long rowIndex;
        
        final int columnIndex;
        
        
        CellPosition(long rowIndex, int columnIndex) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }
        

        @Override
        public int hashCode() {
            return Objects.hash(rowIndex, columnIndex);
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof CellPosition)) {
                return false;
            }
            
            CellPosition otherCellIndex = (CellPosition) other;
            return (
                    otherCellIndex.rowIndex == rowIndex &&
                    otherCellIndex.columnIndex == columnIndex);
        }
        
    }
    
    
    private static class MessengerResultSet implements MiniResultSet {
        
        private final ImmutableList<MiniColumnHeader> columnHeaders;
        
        private final BlockingQueue<ImmutableList<MiniValue>> rowQueue =
                new LinkedBlockingQueue<>();
        
        private volatile long currentRowIndex = -1;
        
        private volatile ImmutableList<MiniValue> currentRow = null; // NOSONAR
        
        private volatile boolean isNextRowFetched = false;
        
        private volatile ImmutableList<MiniValue> nextRow = null; // NOSONAR
        
        private volatile boolean finished = false;
        
        private final ResultSetIterator iterator = new ResultSetIterator();
        
        
        public MessengerResultSet(ImmutableList<MiniColumnHeader> columnHeaders) {
            this.columnHeaders = columnHeaders;
        }
    
        
        @Override
        public ImmutableList<MiniColumnHeader> columnHeaders() {
            return columnHeaders;
        }
        

        @Override
        public Iterator<ImmutableList<MiniValue>> iterator() {
            return iterator;
        }

        private synchronized boolean hasNextForIterator() { // NOSONAR should be here
            if (finished) {
                return false;
            }
            
            if (!isNextRowFetched) {
                nextRow = takeRow();
                isNextRowFetched = true;
            }
            
            if (isEofRow(nextRow)) {
                finished = true;
                return false;
            }
            
            return true;
        }
        
        private synchronized ImmutableList<MiniValue> nextForIterator() { // NOSONAR should be here
            if (!hasNextForIterator()) {
                throw new NoSuchElementException();
            }
            
            closeCurrentRow();
            loadRow();
            
            return currentRow;
        }
        
        private void closeCurrentRow() {
            if (currentRow == null) {
                return;
            }
            
            for (MiniValue value : currentRow) {
                closeValue(value);
            }
        }
        
        private void closeValue(MiniValue value) {
            if (!(value instanceof MessengerValue)) {
                return;
            }
            
            MessengerValue messengerValue = (MessengerValue) value;
            try {
                messengerValue.close();
            } catch (Exception e) {
                // FIXME: what to do?
            }
        }

        private void loadRow() {
            if (isNextRowFetched) {
                currentRow = nextRow;
                nextRow = null;
                isNextRowFetched = false;
            } else {
                currentRow = takeRow();
            }
            
            currentRowIndex++; // NOSONAR
        }
        
        private ImmutableList<MiniValue> takeRow() {
            try {
                return rowQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw closeImplicitly(ExceptionUtil.asUncheckedIOException(e));
            } catch (Exception e) {
                throw closeImplicitly(ExceptionUtil.asUncheckedIOException(e));
            }
        }
        
        private RuntimeException closeImplicitly(RuntimeException existingException) {
            try {
                close();
            } catch (Exception closeException) {
                existingException.addSuppressed(closeException);
            }
            return existingException;
        }
        
        
        private static ImmutableList<MiniValue> eofRow() {
            return ImmutableList.empty();
        }

        private static boolean isEofRow(ImmutableList<MiniValue> row) {
            return row.isEmpty();
        }

        @Override
        public void close() {
            closeCurrentRow();
        }

        
        private class ResultSetIterator implements Iterator<ImmutableList<MiniValue>> {

            @Override
            public boolean hasNext() {
                return hasNextForIterator();
            }

            @Override
            public ImmutableList<MiniValue> next() { // NOSONAR NoSuchElementException is handled
                return nextForIterator();
            }
            
        }

    }

}
