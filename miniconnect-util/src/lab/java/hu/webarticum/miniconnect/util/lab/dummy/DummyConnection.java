package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniResult;

public class DummyConnection implements MiniConnection {

    private volatile boolean closed = false;
    
    
    private final List<QueryExecutor> queryRunners;
    
    
    public DummyConnection() {
        queryRunners = new ArrayList<>();
        queryRunners.add(new DescribeQueryExecutor());
        queryRunners.add(new SelectQueryExecutor());
    }
    
    
    @Override
    public MiniResult execute(String query) {
        if (closed) {
            throw new IllegalStateException("Already closed");
        }
        
        for (QueryExecutor queryRunner : queryRunners) {
            MiniResult result = queryRunner.execute(query);
            if (result != null) {
                return result;
            }
        }
        
        // FIXME: unsuccessful result?
        throw new RuntimeException("Unknow command"); // NOSONAR
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

}