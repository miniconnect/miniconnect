package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.util.data.ImmutableList;

// TODO: move accept methods to an inner object
public class MessengerResultSet implements MiniResultSet {
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;
    
    private final BlockingQueue<ImmutableList<MiniValue>> rowQueue = new LinkedBlockingQueue<>();
    
    private volatile ImmutableList<MiniValue> currentRow; // NOSONAR
    
    private volatile boolean finished = false;
    
    
    public MessengerResultSet(ResultResponse resultResponse) {
        this.columnHeaders = resultResponse.columnHeaders().map(ColumnHeaderData::toMiniColumnHeader);
    }

    public void accept(ResultSetRowsResponse rowsResponse) {
        for (ImmutableList<CellData> rowData : rowsResponse.rows()) {
            addRowData(rowData);
        }
    }
    
    private void addRowData(ImmutableList<CellData> rowData) {
        ImmutableList<MiniValue> row = rowData.map(CellData::toMiniValue);
        try {
            rowQueue.put(row);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void accept(ResultSetValuePartResponse valuePartResponse) {

        // TODO
        
    }

    public void eof() {
        try {
            rowQueue.put(eofRow());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    @Override
    public synchronized ImmutableList<MiniValue> fetch() throws IOException {
        if (finished) {
            // XXX
            return null;
        }
        
        if (currentRow != null) {
            // TODO: close current row
        }

        try {
            currentRow = rowQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (isEofRow(currentRow)) {
            currentRow = null;
            finished = true;
        }
        
        return currentRow;
    }
    
    private ImmutableList<MiniValue> eofRow() {
        return ImmutableList.empty();
    }

    private boolean isEofRow(ImmutableList<MiniValue> row) {
        return row.isEmpty();
    }

    @Override
    public void close() throws IOException {
        
        // TODO
        
    }

}
