package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.server.contentaccess.ChargeableContentAccess;
import hu.webarticum.miniconnect.server.contentaccess.FileChargeableContentAccess;
import hu.webarticum.miniconnect.server.contentaccess.MemoryChargeableContentAccess;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.util.ExceptionUtil;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class MessengerResultSetCharger {
    
    private static final int MAX_MEMORY_CONTENT_SIZE = 10 * 1024 * 1024;
    

    private final MessengerResultSet resultSet;

    
    public MessengerResultSetCharger(ResultResponse resultResponse) {
        this.resultSet = new MessengerResultSet(
                resultResponse.columnHeaders().map(ColumnHeaderData::toMiniColumnHeader));
    }

    
    public void accept(ResultSetRowsResponse rowsResponse) {
        for (ImmutableList<CellData> rowData : rowsResponse.rows()) {
            acceptRow(rowData);
        }
    }
    
    private void acceptRow(ImmutableList<CellData> rowData) {
        
        // TODO:
        //  - create and add value to row
        //  - in case of incomplete value, use the existing charging content access or create
        ImmutableList<MiniValue> row = rowData.mapIndex(
                (i, cellData) -> cellData.toMiniValue(
                        resultSet.columnHeaders.get(i).valueDefinition()));
        
        
        
        try {
            resultSet.rowQueue.put(row);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // FIXME: oops: we don't know the fullLength!
    public void accept(ResultSetValuePartResponse valuePartResponse) {
        
        // TODO:
        // - check resultSet.currentRowIndex, immediately return if higher
        // - use or create the existing charging content access
        // - put the content into the charging content access
        
    }
    
    private ChargeableContentAccess requireChargeable(long rowIndex, int columnIndex, long length) {
        return resultSet.chargeables.computeIfAbsent(
                Pair.of(rowIndex, columnIndex), p -> createChargeable(length));
    }
    
    private ChargeableContentAccess createChargeable(long length) {
        if (length > MAX_MEMORY_CONTENT_SIZE) {
            return new FileChargeableContentAccess(length);
        } else {
            return new MemoryChargeableContentAccess((int) length);
        }
    }

    public void acceptEof() {
        try {
            resultSet.rowQueue.put(MessengerResultSet.eofRow());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public MiniResultSet resultSet() {
        return resultSet;
    }

    
    private static class MessengerResultSet implements MiniResultSet {
        
        private final ImmutableList<MiniColumnHeader> columnHeaders;
        
        private final BlockingQueue<ImmutableList<MiniValue>> rowQueue = new LinkedBlockingQueue<>();
        
        private final Map<Pair<Long, Integer>, ChargeableContentAccess> chargeables =
                Collections.synchronizedMap(new HashMap<>());
        
        private volatile long currentRowIndex = -1;
        
        private volatile ImmutableList<MiniValue> currentRow; // NOSONAR
        
        private volatile boolean finished = false;
        
        
        public MessengerResultSet(ImmutableList<MiniColumnHeader> columnHeaders) {
            this.columnHeaders = columnHeaders;
        }
    
        
        @Override
        public ImmutableList<MiniColumnHeader> columnHeaders() {
            return columnHeaders;
        }
    
        @Override
        public synchronized ImmutableList<MiniValue> fetch() {
            if (finished) {
                // XXX
                return null;
            }
            
            closeCurrentRow();
            fetchNextRow();
            handleEof();
            
            return currentRow;
        }
        
        private void closeCurrentRow() {
            if (currentRow == null) {
                return;
            }
            
            // TODO: do close
        }

        private void fetchNextRow() {
            try {
                currentRow = rowQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw closeImplicitly(ExceptionUtil.asUncheckedIOException(e));
            } catch (Exception e) {
                throw closeImplicitly(ExceptionUtil.asUncheckedIOException(e));
            }

            currentRowIndex++; // NOSONAR
        }
        
        private RuntimeException closeImplicitly(RuntimeException existingException) {
            try {
                close();
            } catch (Exception closeException) {
                existingException.addSuppressed(closeException);
            }
            return existingException;
        }
        
        private void handleEof() {
            if (isEofRow(currentRow)) {
                currentRow = null;
                finished = true;
            }
        }
        
        
        private static ImmutableList<MiniValue> eofRow() {
            return ImmutableList.empty();
        }

        private static boolean isEofRow(ImmutableList<MiniValue> row) {
            return row.isEmpty();
        }

        @Override
        public void close() throws IOException {
            
            // TODO
            
        }
    
    }

}
