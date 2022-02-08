package hu.webarticum.miniconnect.repl.lab.dummy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;

public class DummySession implements MiniSession {
    
    private static final String SQLSTATE_SYNTAXERROR = "42000";
    

    private volatile boolean closed = false;


    private final List<QueryExecutor> queryExecutors;


    public DummySession() {
        queryExecutors = new ArrayList<>();
        queryExecutors.add(new DescribeQueryExecutor());
        queryExecutors.add(new SelectQueryExecutor());
    }


    @Override
    public MiniResult execute(String query) {
        if (closed) {
            throw new IllegalStateException("Already closed");
        }

        for (QueryExecutor queryExecutor : queryExecutors) {
            MiniResult result = queryExecutor.execute(query);
            if (result != null) {
                return result;
            }
        }

        return new StoredResult(new StoredError(1, SQLSTATE_SYNTAXERROR, "Unknow command"));
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        
        throw new UnsupportedOperationException("LOBs are not supported");
    }

    @Override
    public void close() {
        closed = true;
    }

}