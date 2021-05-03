package hu.webarticum.miniconnect.tool.lab.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.api.MiniLobResult;
import hu.webarticum.miniconnect.api.MiniResult;

public class DummySession implements MiniSession {

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

        return new StoredResult("01", "Unknow command");
    }

    @Override
    public MiniLobResult putLargeData(long length, InputStream dataSource) {
        throw new UnsupportedOperationException("LOBs are not supported");
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

}
