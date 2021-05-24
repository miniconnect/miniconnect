package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.api.MiniLobResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.server.message.response.LobResultResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.tool.result.StoredLobResult;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.server.message.request.LobPartRequest;
import hu.webarticum.miniconnect.server.message.request.LobRequest;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerSession implements MiniSession {
    
    private static final int LOB_CHUNK_SIZE = 4096; // TODO: make it configurable
    
    private static final int LOB_RESULT_TIMEOUT_VALUE = 60; // TODO: make it configurable
    
    private static final TimeUnit LOB_RESULT_TIMEOUT_UNIT = TimeUnit.SECONDS; // TODO: make it configurable
    
    
    private final long sessionId;
    
    private final Server server;
    

    private final AtomicInteger requestIdCounter = new AtomicInteger();

    private final AtomicInteger lobIdCounter = new AtomicInteger();


    public MessengerSession(long sessionId, Server server) {
        this.sessionId = sessionId;
        this.server = server;
    }


    @Override
    public MiniResult execute(String query) throws IOException {
        int queryId = requestIdCounter.incrementAndGet();
        int maxRowCount = 0;
        QueryRequest queryRequest = new QueryRequest(sessionId, queryId, query, maxRowCount);
        
        // XXX
        server.accept(queryRequest, r -> System.out.println(r.getClass()));
        
        // TODO
        
        return null;
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
            response = responseFuture.get(LOB_RESULT_TIMEOUT_VALUE, LOB_RESULT_TIMEOUT_UNIT);
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
