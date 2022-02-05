package hu.webarticum.miniconnect.messenger.impl;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

class QueryPartial implements Closeable {
    
    private static final int MAX_CELL_COUNT = 5_000; // TODO: make configurable
    
    private static final int MAX_INTACT_CONTENT_LENGTH = 1_000; // TODO: make configurable
    
    private static final int CONTENT_HEAD_LENGTH = 200; // TODO: make configurable
    
    private static final int CONTENT_CHUNK_LENGTH = 5_000; // TODO: make configurable
    
    
    private final long sessionId;
    
    private final MiniSession session;

    private final ExecutorService fetcherExecutorService = Executors.newCachedThreadPool();
    
    
    public QueryPartial(long sessionId, MiniSession session) {
        this.sessionId = sessionId;
        this.session = session;
    }
    
    
    public void acceptQueryRequest(QueryRequest queryRequest, Consumer<Response> responseConsumer) {
        fetcherExecutorService.submit(() -> invokeExecute(queryRequest, responseConsumer));
    }
    
    private void invokeExecute(QueryRequest queryRequest, Consumer<Response> responseConsumer) {
        String query = queryRequest.query();
        int exchangeId = queryRequest.exchangeId();
        
        MiniResult result = session.execute(query);
        
        responseConsumer.accept(ResultResponse.of(result, sessionId, exchangeId));

        try (MiniResultSet resultSet = result.resultSet()) {
            int columnCount = resultSet.columnHeaders().size();
            int maxRowCount = columnCount == 0 ? 1 : Math.max(1, MAX_CELL_COUNT / columnCount);
            long responseOffset = 0;
            List<ImmutableList<CellData>> responseRowsBuilder = new ArrayList<>();
            List<IncompleteContentHolder> incompleteContents = new ArrayList<>();
            long offset = 0;
            for (ImmutableList<MiniValue> row : resultSet) {
                long r = offset;
                ImmutableList<CellData> responseRow =
                        row.mapIndex((c, v) -> extractCell(
                                exchangeId, r, c, v, incompleteContents));
                responseRowsBuilder.add(responseRow);
                
                offset++;
                
                if (responseRowsBuilder.size() == maxRowCount) {
                    sendRows(
                            exchangeId,
                            responseConsumer,
                            responseOffset,
                            responseRowsBuilder,
                            columnCount);
                    sendChunks(responseConsumer, incompleteContents);
                    responseRowsBuilder.clear();
                    incompleteContents.clear();
                    responseOffset = offset;
                }
            }
            sendRows(
                    exchangeId,
                    responseConsumer,
                    responseOffset,
                    responseRowsBuilder,
                    columnCount);
            sendChunks(responseConsumer, incompleteContents);
            
            responseConsumer.accept(new ResultSetEofResponse(sessionId, exchangeId, offset));
        } catch (Exception e) {
            // FIXME
            e.printStackTrace();
        }
    }
    
    private CellData extractCell(
            int exchangeId,
            long rowIndex,
            int columnIndex,
            MiniValue value,
            List<IncompleteContentHolder> incompleteContents) {
        MiniContentAccess contentAccess = value.contentAccess();
        ByteString headContent;
        if (contentAccess.length() <= MAX_INTACT_CONTENT_LENGTH) {
            headContent = contentAccess.get();
        } else {
            headContent = contentAccess.get(0, CONTENT_HEAD_LENGTH);
            incompleteContents.add(new IncompleteContentHolder(
                    exchangeId, rowIndex, columnIndex, CONTENT_HEAD_LENGTH, contentAccess));
        }
        
        return new CellData(value.isNull(), contentAccess.length(), headContent);
    }

    private void sendRows(
            int exchangeId,
            Consumer<Response> responseConsumer,
            long responseOffset,
            List<ImmutableList<CellData>> responseRowsBuilder,
            int columnCount) {
        ImmutableList<ImmutableList<CellData>> rows = new ImmutableList<>(responseRowsBuilder);
        ImmutableList<Integer> nullables = rangeUntil(columnCount); // FIXME / TODO
        ImmutableMap<Integer, Integer> fixedSizes = ImmutableMap.empty(); // FIXME / TODO
        ResultSetRowsResponse rowsResponse = new ResultSetRowsResponse(
                sessionId, exchangeId, responseOffset, nullables, fixedSizes, rows);
        responseConsumer.accept(rowsResponse);
    }
    
    private ImmutableList<Integer> rangeUntil(int until) {
        List<Integer> rangeBuilder = new ArrayList<>();
        for (int i = 0; i < until; i++) {
            rangeBuilder.add(i);
        }
        return new ImmutableList<>(rangeBuilder);
    }

    private void sendChunks(
            Consumer<Response> responseConsumer,
            List<IncompleteContentHolder> incompleteContents) {
        for (IncompleteContentHolder contentHolder : incompleteContents) {
            long fullLength = contentHolder.contentAccess.length();
            for (
                    long offset = contentHolder.contentOffset;
                    offset < fullLength;
                    offset += CONTENT_CHUNK_LENGTH) {
                long end = offset + CONTENT_CHUNK_LENGTH;
                if (end > fullLength) {
                    end = fullLength;
                }
                int length = (int) (end - offset);
                sendChunk(responseConsumer, contentHolder, offset, length);
            }
            contentHolder.contentAccess.close();
        }
    }

    private void sendChunk(
            Consumer<Response> responseConsumer,
            IncompleteContentHolder contentHolder,
            long offset,
            int length) {
        ByteString contentPart = contentHolder.contentAccess.get(offset, length);
        ResultSetValuePartResponse partResponse = new ResultSetValuePartResponse(
                sessionId,
                contentHolder.exchangeId,
                contentHolder.rowIndex,
                contentHolder.columnIndex,
                offset,
                contentPart);
        responseConsumer.accept(partResponse);
    }

    @Override
    public void close() {
        fetcherExecutorService.shutdownNow();
    }


    private static class IncompleteContentHolder {
        
        final int exchangeId;
        
        final long rowIndex;
        
        final int columnIndex;
        
        final long contentOffset;
        
        final MiniContentAccess contentAccess;
        
        
        IncompleteContentHolder(
                int exchangeId,
                long rowIndex,
                int columnIndex,
                long contentOffset,
                MiniContentAccess contentAccess) {
            this.exchangeId = exchangeId;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.contentOffset = contentOffset;
            this.contentAccess = contentAccess;
        }
        
    }

}
