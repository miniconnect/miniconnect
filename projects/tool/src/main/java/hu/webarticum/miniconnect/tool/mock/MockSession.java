package hu.webarticum.miniconnect.tool.mock;

import java.io.InputStream;
import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;

public class MockSession implements MiniSession {
    
    private final Function<String, MiniResult> resultSupplier;
    

    public MockSession(Function<String, MiniResult> resultSupplier) {
        this.resultSupplier = resultSupplier;
    }
    
    
    @Override
    public MiniResult execute(String query) {
        return resultSupplier.apply(query);
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

}
