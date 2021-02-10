package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.protocol.message.SqlRequest;
import hu.webarticum.miniconnect.util.result.StoredResult;
import hu.webarticum.miniconnect.util.result.StoredResultSetData;
import hu.webarticum.miniconnect.util.value.StoredColumnHeader;
import hu.webarticum.miniconnect.util.value.StoredValue;

public class ClientSession implements MiniConnection {
    
    private final Client client;
    
    private final int sessionId;
    
    private final AtomicInteger queryIdCounter = new AtomicInteger(0);
    

    public ClientSession(Client client, int sessionId) {
        this.client = client;
        this.sessionId = sessionId;
    }
    
    
    @Override
    public MiniResult execute(String query) {
        try {
            return executeThrowing(query);
        } catch (IOException e) {
            
            // XXX
            throw new RuntimeException(e);
            
        }
    }
    
    public MiniResult executeThrowing(String query) throws IOException {
        int queryId = queryIdCounter.getAndIncrement();
        client.send(new SqlRequest(sessionId, queryId, query));
        
        // TODO: wait for ResultResponse
        
        List<MiniColumnHeader> headers = new ArrayList<>();
        headers.add(new StoredColumnHeader("col1", "java.lang.String"));
        headers.add(new StoredColumnHeader("col2", "java.lang.String"));
        List<List<MiniValue>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                new StoredValue("value1.1".getBytes(StandardCharsets.UTF_8)),
                new StoredValue("value1.2".getBytes(StandardCharsets.UTF_8))));
        rows.add(Arrays.asList(
                new StoredValue("value2.1".getBytes(StandardCharsets.UTF_8)),
                new StoredValue("value2.2".getBytes(StandardCharsets.UTF_8))));
        return new StoredResult(new StoredResultSetData(headers, rows));
    }

    @Override
    public boolean isClosed() {
        
        // TODO
        
        return false;
    }

    @Override
    public void close() throws IOException {
        // TODO
        
    }

}
