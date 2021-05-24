package hu.webarticum.miniconnect.server.lab.dummy;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.server.message.request.LobPartRequest;
import hu.webarticum.miniconnect.server.message.request.LobRequest;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.LobResultResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public class DummyServer implements Server {
    
    private static final String LIST_QUERY = "LIST";
    
    private static final String UNEXPECTED_ERROR = "00000";
    
    private static final String BAD_QUERY_ERROR = "00001";
    
    private static final String TOO_LARGE_LOB_ERROR = "00002";
    
    private static final String ILLEGAL_LOB_STATE_ERROR = "00003";
    
    private static final int MAX_LENGTH = 1000_000;
    

    private final AtomicLong rowCounter = new AtomicLong(0);
    
    private final List<List<Object>> data = Collections.synchronizedList(new ArrayList<>());
    
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
        }
    }

    private void acceptQueryRequest(QueryRequest request, Consumer<Response> responseConsumer) {
        Objects.requireNonNull(responseConsumer);
        
        long sessionId = request.sessionId();
        int queryId = request.id();
        String query = request.query();
        
        if (!query.equals(LIST_QUERY)) {
            ResultResponse resultResponse = new ResultResponse(
                    sessionId,
                    queryId,
                    false,
                    BAD_QUERY_ERROR,
                    "Only LIST query is supported",
                    ImmutableList.empty(),
                    true,
                    ImmutableList.empty());
            responseConsumer.accept(resultResponse);
        }
        
        
        // XXX
        
        ResultResponse resultResponse = new ResultResponse(
                sessionId, queryId, true, "", "", ImmutableList.empty(), true, ImmutableList.empty());
        responseConsumer.accept(resultResponse);
        
        ImmutableList<ImmutableList<CellData>> rows = new ImmutableList<>();
        
        ResultSetRowsResponse resultSetRowsResponse = new ResultSetRowsResponse(
                sessionId, queryId, 0, ImmutableList.empty(), ImmutableMap.empty(), rows);
        responseConsumer.accept(resultSetRowsResponse);
        
        // TODO
        
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
        } else if (length == 0L) {
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
                    new LobResultResponse(sessionId, lobId, false, ILLEGAL_LOB_STATE_ERROR, "Illegal LOB state", ""));
            return;
        } catch (Exception e) {
            removeCompletable(
                    contentId,
                    new LobResultResponse(sessionId, lobId, false, UNEXPECTED_ERROR, "Unexpected error " + e.getMessage(), ""));
            return;
        }
        
        if (completable.completed()) {
            ByteString fullContent = completable.content();
            addRow(fullContent);
            Consumer<Response> responseConsumer = removeCompletable(contentId, null);
            if (responseConsumer != null) {
                String variableName = "blob_" + contentId; // XXX
                responseConsumer.accept(new LobResultResponse(sessionId, lobId, true, "", "", variableName));
            }
        }
    }
    
    private CompletableSmallLobContent requireCompletable(long contentId, Consumer<Response> responseConsumer) {
        CompletableSmallLobContent completable;
        synchronized (incompleteContents) {
            completable = incompleteContents.get(contentId);
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
            
            // XXX
            System.out.println(data);
        }
    }
    
    private String currentTimestamp() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date());
    }

}
