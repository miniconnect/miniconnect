package hu.webarticum.miniconnect.server.lab.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueEncoder;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.server.message.request.LobPartRequest;
import hu.webarticum.miniconnect.server.message.request.LobRequest;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.LobResultResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.server.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.server.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.tool.result.DefaultValueEncoder;
import hu.webarticum.miniconnect.tool.result.StoredValue;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public class DummyServer implements Server {
    
    private static final String UNEXPECTED_ERROR = "00000";
    
    private static final String BAD_QUERY_ERROR = "00001";
    
    private static final String TOO_LARGE_LOB_ERROR = "00002";
    
    private static final String ILLEGAL_LOB_STATE_ERROR = "00003";
    
    private static final int MAX_LENGTH = 1000_000;
    
    private static final int LOB_INITIAL_LENGTH = 25;
    
    private static final int LOB_CHUNK_LENGTH = 10;
    
    public static final Pattern SELECT_ALL_QUERY_PATTERN = Pattern.compile(
            "(?i)\\s*SELECT\\s+\\*\\s+FROM\\s+([\"`]?)(?-i)data(?i)\\1\\s*;?\\s*");
    
    
    private static final ImmutableList<String> columnNames = ImmutableList.of(
            "id", "created_at", "length", "content");

    private static final ImmutableList<MiniValueEncoder> encoders = ImmutableList.of(
            new DefaultValueEncoder(Long.class),
            new DefaultValueEncoder(String.class),
            new DefaultValueEncoder(Integer.class),
            new DefaultValueEncoder(String.class));
    

    private final AtomicLong rowCounter = new AtomicLong(0);
    
    private final List<List<Object>> data = new ArrayList<>();
    
    private final Map<Long, CompletableSmallLobContent> incompleteContents = new HashMap<>();
    
    private final Map<Long, Consumer<Response>> lobResponseConsumers = new HashMap<>();
    
    
    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof QueryRequest) {
            acceptQueryRequest((QueryRequest) request, responseConsumer);
        } else if (request instanceof LobRequest) {
            acceptLobRequest((LobRequest) request, responseConsumer);
        } else if (request instanceof LobPartRequest) {
            acceptLobPartRequest((LobPartRequest) request);
        } else {
            throw new UnsupportedOperationException(String.format(
                    "Unsupported request type: %s",
                    request.getClass().getSimpleName()));
        }
    }

    private void acceptQueryRequest(QueryRequest request, Consumer<Response> responseConsumer) {
        Objects.requireNonNull(responseConsumer);
        
        long sessionId = request.sessionId();
        int queryId = request.id();
        String query = request.query();
        
        if (!SELECT_ALL_QUERY_PATTERN.matcher(query).matches()) {
            sendBadQueryErrorResultResponse(request, responseConsumer);
            return;
        }
        
        List<List<Object>> dataRows;
        synchronized (data) {
            dataRows = new ArrayList<>(data);
        }
        int dataRowCount = dataRows.size();
        
        List<ColumnHeaderData> headerDatasBuilder = new ArrayList<>();
        int columnCount = encoders.size();
        for (int i = 0; i < columnCount; i++) {
            MiniValueEncoder encoder = encoders.get(i);
            String columnName = columnNames.get(i);
            MiniColumnHeader header = encoder.headerFor(columnName);
            headerDatasBuilder.add(new ColumnHeaderData(header));
        }
        ImmutableList<ColumnHeaderData> headerDatas = new ImmutableList<>(headerDatasBuilder);
        
        ResultResponse resultResponse = new ResultResponse(
                sessionId, queryId, true, "", "", ImmutableList.empty(), true, headerDatas);
        responseConsumer.accept(resultResponse);
        
        for (int fetchFrom = 0; fetchFrom < dataRowCount; fetchFrom += 3) {
            int fetchUntil = Math.min(dataRowCount, fetchFrom + 3);
            List<List<Object>> dataRowsChunk = dataRows.subList(fetchFrom, fetchUntil);
            sendRows(dataRowsChunk, sessionId, queryId, fetchFrom, responseConsumer);
        }
        
        ResultSetEofResponse resultSetEofResponse = new ResultSetEofResponse(
                sessionId, queryId, dataRows.size());
        responseConsumer.accept(resultSetEofResponse);
    }
    
    private void sendRows(
            List<List<Object>> dataRows,
            long sessionId,
            int queryId,
            long offset,
            Consumer<Response> responseConsumer) {

        int rowCount = dataRows.size();
        int columnCount = encoders.size();
        
        List<ResultSetValuePartResponse> partResponses = new ArrayList<>();
        
        List<ImmutableList<CellData>> rowsBuilder = new ArrayList<>();
        for (int r = 0; r < rowCount; r++) {
            List<Object> dataRow = dataRows.get(r);
            List<CellData> rowBuilder = new ArrayList<>();
            for (int c = 0; c < columnCount; c++) {
                MiniValueEncoder encoder = encoders.get(c);
                Object content = dataRow.get(c);
                MiniValue value = encoder.encode(content);
                if ((value.isLob() && value.length() > 0) || value.length() > LOB_INITIAL_LENGTH) {
                    try (InputStream valueIn = value.lobAccess().inputStream()) {
                        value = new StoredValue(readInputStream(valueIn, LOB_INITIAL_LENGTH));
                        ByteString chunk;
                        while (!(chunk = readInputStream(valueIn, LOB_CHUNK_LENGTH)).isEmpty()) {
                            partResponses.add(new ResultSetValuePartResponse(
                                    sessionId, queryId, offset + r, c, offset, chunk));
                        }
                    } catch (IOException e) {
                        // FIXME: what to do?
                    }
                }
                rowBuilder.add(new CellData(value)); // FIXME
            }
            rowsBuilder.add(new ImmutableList<>(rowBuilder));
        }
        ImmutableList<ImmutableList<CellData>> rows = new ImmutableList<>(rowsBuilder);
        
        ResultSetRowsResponse resultSetRowsResponse = new ResultSetRowsResponse(
                sessionId, queryId, offset, ImmutableList.empty(), ImmutableMap.empty(), rows);
        responseConsumer.accept(resultSetRowsResponse);
        
        for (ResultSetValuePartResponse partResponse : partResponses) {
            responseConsumer.accept(partResponse);
        }
    }

    private ByteString readInputStream(InputStream in, int length) throws IOException {
        byte[] buffer = new byte[length];
        int readLength = in.read(buffer);
        if (readLength == -1) {
            return ByteString.empty();
        }
        if (readLength == length) {
            return ByteString.wrap(buffer);
        }
        byte[] result = new byte[readLength];
        System.arraycopy(buffer, 0, result, 0, readLength);
        return ByteString.wrap(result);
    }
    
    private void sendBadQueryErrorResultResponse(QueryRequest request, Consumer<Response> responseConsumer) {
        ResultResponse resultResponse = new ResultResponse(
                request.sessionId(),
                request.id(),
                false,
                BAD_QUERY_ERROR,
                "Bad query, only select all is supported",
                ImmutableList.empty(),
                true,
                ImmutableList.empty());
        responseConsumer.accept(resultResponse);
    }

    private void acceptLobRequest(LobRequest request, Consumer<Response> responseConsumer) {
        Objects.requireNonNull(responseConsumer);
        
        long sessionId = request.sessionId();
        int lobId = request.id();
        long length = request.length();
        
        if (length > MAX_LENGTH) {
            responseConsumer.accept(new LobResultResponse(
                    sessionId, lobId, false, TOO_LARGE_LOB_ERROR, "Too large LOB", ""));
            return;
        }
        
        Long contentId = (sessionId * 1000) + lobId;
        CompletableSmallLobContent completable = requireCompletable(contentId, responseConsumer);
        
        try {
            completable.setLength((int) length);
        } catch (IllegalStateException e) {
            incompleteContents.remove(contentId);
            responseConsumer.accept(new LobResultResponse(
                    sessionId, lobId, false, ILLEGAL_LOB_STATE_ERROR, "Illegal LOB state", ""));
        } catch (Exception e) {
            incompleteContents.remove(contentId);
            responseConsumer.accept(new LobResultResponse(
                    sessionId, lobId, false, UNEXPECTED_ERROR, "Unexpected error " + e.getMessage(), ""));
        }
        
        if (length == 0) {
            acceptLobPartRequest(new LobPartRequest(sessionId, lobId, 0, ByteString.wrap(new byte[0])));
        }
    }

    private void acceptLobPartRequest(LobPartRequest request) {
        long sessionId = request.sessionId();
        int lobId = request.lobId();
        long offset = request.offset();
        ByteString content = request.content();

        if (offset > MAX_LENGTH) {
            // XXX
            return;
        }
        
        Long contentId = (sessionId * 1000) + lobId;
        CompletableSmallLobContent completable = requireCompletable(contentId, null);
        
        try {
            completable.put((int) offset, content);
        } catch (IllegalStateException e) {
            removeCompletable(
                    contentId,
                    new LobResultResponse(
                            sessionId,
                            lobId,
                            false,
                            ILLEGAL_LOB_STATE_ERROR,
                            "Illegal LOB state",
                            ""));
            return;
        } catch (Exception e) {
            removeCompletable(
                    contentId,
                    new LobResultResponse(
                            sessionId,
                            lobId,
                            false,
                            UNEXPECTED_ERROR,
                            "Unexpected error " + e.getMessage(),
                            ""));
            return;
        }
        
        if (completable.completed()) {
            ByteString fullContent = completable.content();
            addRow(fullContent);
            Consumer<Response> responseConsumer = removeCompletable(contentId, null);
            if (responseConsumer != null) {
                String variableName = "blob_" + contentId;
                responseConsumer.accept(new LobResultResponse(sessionId, lobId, true, "", "", variableName));
            }
        }
    }
    
    private CompletableSmallLobContent requireCompletable(long contentId, Consumer<Response> responseConsumer) {
        CompletableSmallLobContent completable;
        synchronized (incompleteContents) {
            completable = incompleteContents.get(contentId); // NOSONAR
            if (completable == null) {
                completable = new CompletableSmallLobContent();
                incompleteContents.put(contentId, completable);
            }
        }
        if (responseConsumer != null) {
            synchronized (lobResponseConsumers) {
                lobResponseConsumers.put(contentId, responseConsumer);
            }
        }
        return completable;
    }

    private Consumer<Response> removeCompletable(long contentId, Response errorResponse) {
        synchronized (incompleteContents) {
            incompleteContents.remove(contentId);
        }
        Consumer<Response> responseConsumer;
        synchronized (lobResponseConsumers) {
            responseConsumer = lobResponseConsumers.remove(contentId);
        }
        if (errorResponse != null) {
            if (responseConsumer != null) {
                responseConsumer.accept(errorResponse);
            } else {
                
                // TODO
                
            }
        }
        return responseConsumer;
    }
    
    private void addRow(ByteString content) {
        Long rowId = rowCounter.incrementAndGet();
        String insertTimestamp = currentTimestamp();
        String stringContent = content.toString(StandardCharsets.UTF_8);
        
        List<Object> row = new ArrayList<>();
        row.add(rowId);
        row.add(insertTimestamp);
        row.add(stringContent.length());
        row.add(stringContent);
        
        synchronized (data) {
            data.add(row);
        }
    }
    
    private String currentTimestamp() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date());
    }

}
