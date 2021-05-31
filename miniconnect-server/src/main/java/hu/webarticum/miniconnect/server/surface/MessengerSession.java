package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniLobResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.server.message.response.LobResultResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.server.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.server.util.OrderAligningQueue;
import hu.webarticum.miniconnect.tool.result.StoredLobResult;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.tool.result.StoredResultSetData;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.server.message.request.LobPartRequest;
import hu.webarticum.miniconnect.server.message.request.LobRequest;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;

// TODO: use asynchronously fillable result set
public class MessengerSession implements MiniSession {
    
    private static final int LOB_CHUNK_SIZE = 4096; // TODO: make it configurable
    
    private static final int RESULT_TIMEOUT_VALUE = 60; // TODO: make it configurable
    
    private static final TimeUnit RESULT_TIMEOUT_UNIT = TimeUnit.SECONDS; // TODO: make it configurable
    
    
    private final long sessionId;
    
    private final Server server;
    

    private final AtomicInteger requestIdCounter = new AtomicInteger();

    private final AtomicInteger lobIdCounter = new AtomicInteger();


    public MessengerSession(long sessionId, Server server) {
        this.sessionId = sessionId;
        this.server = server;
    }


    // TODO: handle lob value parts
    @Override
    public MiniResult execute(String query) throws IOException {
        int queryId = requestIdCounter.incrementAndGet();
        int maxRowCount = 0;
        
        OrderAligningQueue<Response> responseQueue = new OrderAligningQueue<>(
                MessengerSession::checkNextResultResponse);
        
        QueryRequest queryRequest = new QueryRequest(sessionId, queryId, query, maxRowCount);
        server.accept(queryRequest, responseQueue::add);
        
        ResultResponse resultResponse = null;
        List<List<MiniValue>> rows = new ArrayList<>();
        while (true) { // NOSONAR
            Response response;
            try {
                response = responseQueue.take(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new StoredResult("INTERRUPT", "Interrupt occured while waiting for results");
            } catch (TimeoutException e) {
                return new StoredResult("TIMEOUT", "Timeout reached while waiting for results");
            }
            
            if (response instanceof ResultResponse) {
                resultResponse = (ResultResponse) response;
                if (!resultResponse.success()) {
                    break;
                }
            } else if (response instanceof ResultSetRowsResponse) {
                ResultSetRowsResponse rowsResponse = (ResultSetRowsResponse) response;
                for (ImmutableList<CellData> cellDatas : rowsResponse.rows()) {
                    List<MiniValue> row = new ArrayList<>();
                    for (CellData cellData : cellDatas) {
                        row.add(cellData.toMiniValue());
                    }
                    rows.add(row);
                }
            } else if (response instanceof ResultSetValuePartResponse) {
                
                // TODO
                ResultSetValuePartResponse valuePartResponse = (ResultSetValuePartResponse) response;
                System.out.println("  Content part: " + valuePartResponse.content().length());
                
            } else if (response instanceof ResultSetEofResponse) {
                break;
            }
        }
        
        if (resultResponse == null) {
            throw new IllegalStateException("resultResponse could not be null");
        }
        
        if (!resultResponse.success()) {
            return new StoredResult(resultResponse.errorCode(), resultResponse.errorMessage());
        }
        
        List<MiniColumnHeader> columnHeaders = new ArrayList<>();
        for (ColumnHeaderData columnHeaderData : resultResponse.columnHeaders()) {
            columnHeaders.add(columnHeaderData.toMiniColumnHeader());
        }
        StoredResultSetData resultSetData = new StoredResultSetData(columnHeaders, rows);
        return new StoredResult(resultSetData);
    }
    
    private static boolean checkNextResultResponse(Response previousResponse, Response response) {
        if (response instanceof ResultResponse) {
            return previousResponse == null;
        } else if (response instanceof ResultSetRowsResponse) {
            ResultSetRowsResponse resultSetRowsResponse = (ResultSetRowsResponse) response;
            long rowOffset = resultSetRowsResponse.rowOffset();
            return checkNextOffset(previousResponse, rowOffset);
        } else if (response instanceof ResultSetEofResponse) {
            ResultSetEofResponse resultSetEofResponse = (ResultSetEofResponse) response;
            long endOffset = resultSetEofResponse.endOffset();
            return checkNextOffset(previousResponse, endOffset);
        } else {
            return false;
        }
    }
    
    private static boolean checkNextOffset(Response previousResponse, long nextOffset) {
        if (nextOffset == 0L) {
            return true;
        }
        if (!(previousResponse instanceof ResultSetRowsResponse)) {
            return false;
        }
        
        ResultSetRowsResponse previousResultSetRowsResponse = (ResultSetRowsResponse) previousResponse;
        long previousRowOffset = previousResultSetRowsResponse.rowOffset();
        int previousRowCount = previousResultSetRowsResponse.rows().size();
        long previousEndOffset = previousRowOffset + previousRowCount;
        
        return nextOffset == previousEndOffset;
    }

    @Override
    public MiniLobResult putLargeData(long length, InputStream dataSource) throws IOException {
        int lobId = lobIdCounter.incrementAndGet();
        
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        
        LobRequest lobRequest = new LobRequest(sessionId, lobId, length);
        server.accept(lobRequest, responseFuture::complete);

        byte[] buffer = new byte[LOB_CHUNK_SIZE];
        int readSize = 0;
        long offset = 0;
        while ((readSize = dataSource.read(buffer)) != -1) {
            // TODO: check for error
            ByteString content = ByteString.wrap(Arrays.copyOf(buffer, readSize));
            LobPartRequest lobPartRequest = new LobPartRequest(sessionId, lobId, offset, content);
            server.accept(lobPartRequest);
            offset += readSize;
        }
        
        Response response = null;
        try {
            response = responseFuture.get(RESULT_TIMEOUT_VALUE, RESULT_TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            // nothing to do
        }

        if (response instanceof LobResultResponse) {
            LobResultResponse lobResultResponse = (LobResultResponse) response;
            return new StoredLobResult(
                    lobResultResponse.success(),
                    lobResultResponse.errorCode(),
                    lobResultResponse.errorMessage(),
                    lobResultResponse.getVariableName());
        } else if (response == null) {
            return new StoredLobResult(false, "99990", "No response", ""); // XXX
        } else {
            return new StoredLobResult(false, "99999", "Bad response", ""); // XXX
        }
    }
    
    @Override
    public void close() throws IOException {
        
        // TODO

    }

}
