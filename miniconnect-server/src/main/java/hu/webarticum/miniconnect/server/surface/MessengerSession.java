package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniLobResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.LobResultResponse;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.util.FutureUtil;
import hu.webarticum.miniconnect.server.util.Offeror;
import hu.webarticum.miniconnect.tool.result.StoredLobResult;
import hu.webarticum.miniconnect.server.message.request.LobPartRequest;
import hu.webarticum.miniconnect.server.message.request.LobRequest;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerSession implements MiniSession {
    
    private static final int LOB_CHUNK_SIZE = 4096; // TODO: make it configurable
    
    private static final int LOB_RESULT_TIMEOUT_VALUE = 60; // TODO: make it configurable
    
    private static final TimeUnit LOB_RESULT_TIMEOUT_UNIT = TimeUnit.SECONDS; // TODO: make it configurable
    
    
    private final long sessionId;
    
    private final Consumer<Request> requestConsumer;
    
    private final Offeror<Response> responseOfferor;
    

    private final AtomicInteger requestIdCounter = new AtomicInteger();

    private final AtomicInteger lobIdCounter = new AtomicInteger();


    public MessengerSession(
            long sessionId,
            Consumer<Request> requestConsumer,
            Offeror<Response> responseOfferor) {
        
        this.sessionId = sessionId;
        this.requestConsumer = requestConsumer;
        this.responseOfferor = responseOfferor;
    }


    @Override
    public MiniResult execute(String query) throws IOException {
        
        // TODO
        
        return null;
    }

    @Override
    public MiniLobResult putLargeData(long length, InputStream dataSource) throws IOException {
        int lobId = lobIdCounter.incrementAndGet();
        
        CompletableFuture<LobResultResponse> responseFuture = new CompletableFuture<>();
        Offeror.Listening listening = responseOfferor.listen(r -> {
            if (!(r instanceof LobResultResponse)) {
                return false;
            }
            
            LobResultResponse lobResultResponse = (LobResultResponse) r;
            if (lobResultResponse.sessionId() != sessionId || lobResultResponse.lobId() != lobId) {
                return false;
            }
            
            responseFuture.complete(lobResultResponse);
            return true;
        });
        
        LobRequest lobRequest = new LobRequest(sessionId, lobId, length);
        requestConsumer.accept(lobRequest);
        
        byte[] buffer = new byte[LOB_CHUNK_SIZE];
        int readSize = 0;
        long offset = 0;
        while ((readSize = dataSource.read(buffer)) != -1) {
            ByteString content = ByteString.wrap(Arrays.copyOf(buffer, readSize));
            LobPartRequest lobPartRequest = new LobPartRequest(sessionId, lobId, offset, content);
            requestConsumer.accept(lobPartRequest);
            offset += readSize;
        }
        
        Optional<LobResultResponse> optionalResult = FutureUtil.getSilently(
                responseFuture, LOB_RESULT_TIMEOUT_VALUE, LOB_RESULT_TIMEOUT_UNIT);
        
        listening.close();
        
        if (optionalResult.isEmpty()) {
            throw new IOException("No lob result given");
        }
        
        LobResultResponse lobResultResponse = optionalResult.get();
        
        return new StoredLobResult(
                lobResultResponse.success(),
                lobResultResponse.errorCode(),
                lobResultResponse.errorMessage(),
                lobResultResponse.getVariableName());
    }
    
    @Override
    public void close() throws IOException {
        
        // TODO

    }

}
