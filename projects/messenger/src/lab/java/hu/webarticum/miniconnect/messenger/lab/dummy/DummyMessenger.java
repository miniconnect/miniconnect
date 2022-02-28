package hu.webarticum.miniconnect.messenger.lab.dummy;

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
import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.request.SessionCloseRequest;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionCloseResponse;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;
import hu.webarticum.regexbee.Bee;

public class DummyMessenger implements Messenger {

    private static final String SQLSTATE_SYNTAXERROR = "42000";
    
    private static final int MAX_LENGTH = 1000_000;
    
    // FIXME/TODO: larger value
    private static final int DATA_CHUNK_LENGTH = 20;
    

    private static final Pattern SELECT_ALL_QUERY_PATTERN = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("SELECT")).then(Bee.WHITESPACE.more())
            .then(Bee.fixed("*")).then(Bee.WHITESPACE.more())
            .then(Bee.fixed("FROM")).then(Bee.WHITESPACE.more())
            .then(Bee.oneCharOf("\"`").optional().as("quote"))
            .then(Bee.without(Pattern.CASE_INSENSITIVE, Bee.fixed("data")))
            .then(Bee.ref("quote")).then(Bee.WHITESPACE.any())
            .then(Bee.fixed(";").optional()).then(Bee.WHITESPACE.any())
            .toPattern(Pattern.CASE_INSENSITIVE);
    
    private static final ImmutableList<MiniColumnHeader> columnHeaders = ImmutableList.of(
            headerOf("id", false, StandardValueType.LONG),
            headerOf("created_at", false, StandardValueType.STRING),
            headerOf("name", false, StandardValueType.STRING),
            headerOf("length", false, StandardValueType.STRING),
            headerOf("content", false, StandardValueType.STRING));


    private final AtomicLong sessionIdCounter = new AtomicLong(0);
    
    private final AtomicLong rowCounter = new AtomicLong(0);
    
    private final List<List<Object>> data = new ArrayList<>();
    
    private final Map<Long, CompletableSmallContent> incompleteContents = new HashMap<>();
    
    private final Map<Long, String> variableNames = new HashMap<>();
    
    private final Map<Long, Consumer<Response>> dataSaveConsumers = new HashMap<>();
    
    private final ImmutableList<ValueTranslator> translators = columnHeaders.map(
            h -> StandardValueType.valueOf(h.valueDefinition().type()).defaultTranslator());
    

    private static final MiniColumnHeader headerOf(
            String name, boolean isNullable, StandardValueType valueType) {
        return new StoredColumnHeader(
                name, isNullable, new StoredValueDefinition(valueType.name()));
    }
    
    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof QueryRequest) {
            acceptQueryRequest((QueryRequest) request, responseConsumer);
        } else if (request instanceof LargeDataHeadRequest) {
            acceptLargeDataHeadRequest((LargeDataHeadRequest) request, responseConsumer);
        } else if (request instanceof LargeDataPartRequest) {
            acceptLargeDataPartRequest((LargeDataPartRequest) request);
        } else if (request instanceof SessionInitRequest) {
            responseConsumer.accept(new SessionInitResponse(sessionIdCounter.incrementAndGet()));
        } else if (request instanceof SessionCloseRequest) {
            acceptSessionCloseRequest((SessionCloseRequest) request, responseConsumer);
        } else {
            throw new UnsupportedOperationException(String.format(
                    "Unsupported request type: %s",
                    request.getClass().getSimpleName()));
        }
    }

    private void acceptQueryRequest(QueryRequest request, Consumer<Response> responseConsumer) {
        Objects.requireNonNull(responseConsumer);
        
        long sessionId = request.sessionId();
        int exchangeId = request.exchangeId();
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
        
        List<ColumnHeaderData> headerDatasBuilder = new ArrayList<>(columnHeaders.size());
        for (MiniColumnHeader header : columnHeaders) {
            headerDatasBuilder.add(ColumnHeaderData.of(header));
        }
        
        ImmutableList<ColumnHeaderData> headerDatas =
                ImmutableList.fromCollection(headerDatasBuilder);
        
        ResultResponse resultResponse = new ResultResponse(
                sessionId,
                exchangeId,
                true,
                new ResultResponse.ErrorData(0, "00000", ""),
                ImmutableList.empty(),
                true,
                headerDatas);
        responseConsumer.accept(resultResponse);
        
        for (int fetchFrom = 0; fetchFrom < dataRowCount; fetchFrom += 3) {
            int fetchUntil = Math.min(dataRowCount, fetchFrom + 3);
            List<List<Object>> dataRowsChunk = dataRows.subList(fetchFrom, fetchUntil);
            sendRows(dataRowsChunk, sessionId, exchangeId, fetchFrom, responseConsumer);
        }
        
        ResultSetEofResponse resultSetEofResponse = new ResultSetEofResponse(
                sessionId, exchangeId, dataRows.size());
        responseConsumer.accept(resultSetEofResponse);
    }
    
    private void sendRows(
            List<List<Object>> dataRows,
            long sessionId,
            int exchangeId,
            long offset,
            Consumer<Response> responseConsumer) {
        
        int rowCount = dataRows.size();
        int columnCount = columnHeaders.size();
        
        List<ResultSetValuePartResponse> partResponses = new ArrayList<>();
        
        List<ImmutableList<CellData>> rowsBuilder = new ArrayList<>();
        for (int r = 0; r < rowCount; r++) {
            List<Object> dataRow = dataRows.get(r);
            List<CellData> rowBuilder = new ArrayList<>();
            for (int c = 0; c < columnCount; c++) {
                Object content = dataRow.get(c);
                ValueTranslator translator = translators.get(c);
                MiniValue value = translator.encodeFully(content);
                MiniContentAccess contentAccess = value.contentAccess();
                long fullLength = contentAccess.length();
                
                if (fullLength > DATA_CHUNK_LENGTH) {
                    try (InputStream valueIn = value.contentAccess().inputStream()) {
                        ByteString firstChunk = readInputStream(valueIn, DATA_CHUNK_LENGTH);
                        rowBuilder.add(new CellData(false, fullLength, firstChunk));
                        
                        long contentOffset = firstChunk.length();
                        ByteString chunk;
                        while (!(chunk = readInputStream(valueIn, DATA_CHUNK_LENGTH)).isEmpty()) {
                            partResponses.add(new ResultSetValuePartResponse(
                                    sessionId, exchangeId, offset + r, c, contentOffset, chunk));
                            contentOffset += chunk.length();
                        }
                    } catch (IOException e) {
                        // FIXME: what to do?
                    }
                } else {
                    rowBuilder.add(CellData.of(value));
                }
            }
            rowsBuilder.add(ImmutableList.fromCollection(rowBuilder));
        }
        ImmutableList<ImmutableList<CellData>> rows = ImmutableList.fromCollection(rowsBuilder);
        
        ResultSetRowsResponse resultSetRowsResponse = new ResultSetRowsResponse(
                sessionId, exchangeId, offset, ImmutableList.empty(), ImmutableMap.empty(), rows);
        responseConsumer.accept(resultSetRowsResponse);
        
        for (ResultSetValuePartResponse partResponse : partResponses) {
            responseConsumer.accept(partResponse);
        }
    }

    private ByteString readInputStream(InputStream in, int maxLength) throws IOException {
        byte[] buffer = new byte[maxLength];
        int readLength = in.read(buffer);
        if (readLength == -1) {
            return ByteString.empty();
        }
        if (readLength == maxLength) {
            return ByteString.wrap(buffer);
        }
        byte[] result = new byte[readLength];
        System.arraycopy(buffer, 0, result, 0, readLength);
        return ByteString.wrap(result);
    }
    
    private void sendBadQueryErrorResultResponse(
            QueryRequest request, Consumer<Response> responseConsumer) {
        ResultResponse resultResponse = new ResultResponse(
                request.sessionId(),
                request.exchangeId(),
                false,
                new ResultResponse.ErrorData(
                        1,
                        SQLSTATE_SYNTAXERROR,
                        "Bad query, try select from 'data' (put data as LOB)"),
                ImmutableList.empty(),
                true,
                ImmutableList.empty());
        responseConsumer.accept(resultResponse);
    }

    private void acceptLargeDataHeadRequest(
            LargeDataHeadRequest request, Consumer<Response> consumer) {
        
        Objects.requireNonNull(consumer);
        
        long sessionId = request.sessionId();
        int exchangeId = request.exchangeId();
        String variableName = request.variableName();
        long length = request.length();
        
        if (length > MAX_LENGTH) {
            consumer.accept(new LargeDataSaveResponse(
                    sessionId, exchangeId, false, 2, "XXXXX", "Too large LOB"));
            return;
        }
        
        Long contentId = (sessionId * 1000) + exchangeId;
        CompletableSmallContent completable = requireCompletable(contentId, consumer);
        
        synchronized (variableNames) {
            variableNames.put(contentId, variableName);
        }
        
        try {
            completable.setLength((int) length);
        } catch (IllegalStateException e) {
            incompleteContents.remove(contentId);
            consumer.accept(new LargeDataSaveResponse(
                    sessionId,
                    exchangeId,
                    false,
                    3,
                    "XXXXX",
                    "Illegal LOB state"));
        } catch (Exception e) {
            incompleteContents.remove(contentId);
            consumer.accept(new LargeDataSaveResponse(
                    sessionId,
                    exchangeId,
                    false,
                    4,
                    "XXXXX",
                    "Unexpected error: " + e.getMessage()));
        }
        
        if (length == 0) {
            acceptLargeDataPartRequest(new LargeDataPartRequest(
                    sessionId, exchangeId, 0, ByteString.empty()));
        }
    }

    private void acceptLargeDataPartRequest(LargeDataPartRequest request) {
        long sessionId = request.sessionId();
        int exchangeId = request.exchangeId();
        long offset = request.offset();
        ByteString content = request.content();

        if (offset > MAX_LENGTH) {
            // XXX
            return;
        }
        
        Long contentId = (sessionId * 1000) + exchangeId;
        CompletableSmallContent completable = requireCompletable(contentId, null);
        
        try {
            completable.put((int) offset, content);
        } catch (IllegalStateException e) {
            removeCompletable(
                    contentId,
                    new LargeDataSaveResponse(
                            sessionId,
                            exchangeId,
                            false,
                            3,
                            "D0003",
                            "Illegal LOB state"));
            return;
        } catch (Exception e) {
            removeCompletable(
                    contentId,
                    new LargeDataSaveResponse(
                            sessionId,
                            exchangeId,
                            false,
                            99,
                            "XXXXX",
                            "Unexpected error " + e.getMessage()));
            return;
        }
        
        if (completable.completed()) {
            String variableName = variableNames.get(contentId);
            if (variableName == null) {
                throw new IllegalStateException("No variable name given");
            }
            ByteString fullContent = completable.content();
            addRow(variableName, fullContent);
            Consumer<Response> responseConsumer = removeCompletable(contentId, null);
            if (responseConsumer != null) {
                responseConsumer.accept(new LargeDataSaveResponse(
                        sessionId,
                        exchangeId,
                        true,
                        0,
                        "00000",
                        ""));
            }
        }
    }
    
    private CompletableSmallContent requireCompletable(
            long contentId, Consumer<Response> consumer) {
        
        CompletableSmallContent completable;
        synchronized (incompleteContents) {
            completable = incompleteContents.get(contentId); // NOSONAR
            if (completable == null) {
                completable = new CompletableSmallContent();
                incompleteContents.put(contentId, completable);
            }
        }
        if (consumer != null) {
            synchronized (dataSaveConsumers) {
                dataSaveConsumers.put(contentId, consumer);
            }
        }
        return completable;
    }

    private Consumer<Response> removeCompletable(long contentId, Response errorResponse) {
        synchronized (incompleteContents) {
            incompleteContents.remove(contentId);
        }
        synchronized (variableNames) {
            variableNames.remove(contentId);
        }
        Consumer<Response> responseConsumer;
        synchronized (dataSaveConsumers) {
            responseConsumer = dataSaveConsumers.remove(contentId);
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
    
    private void addRow(String variableName, ByteString content) {
        Long rowId = rowCounter.incrementAndGet();
        String insertTimestamp = currentTimestamp();
        String stringContent = content.toString(StandardCharsets.UTF_8);
        
        List<Object> row = new ArrayList<>();
        row.add(rowId);
        row.add(insertTimestamp);
        row.add(variableName);
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

    private void acceptSessionCloseRequest(
            SessionCloseRequest request, Consumer<Response> responseConsumer) {
        responseConsumer.accept(
                new SessionCloseResponse(request.sessionId(), request.exchangeId()));
    }

}
