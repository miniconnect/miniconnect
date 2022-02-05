package hu.webarticum.miniconnect.impl.mock;

import java.io.InputStream;
import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;

public class MockSession implements MiniSession {
    
    private final Function<String, MiniResult> resultProvider;
    
    private final PutLargeDataFunction putLargeDataFunction;
    

    public MockSession(Function<String, MiniResult> resultSupplier) {
        this(resultSupplier, null);
    }

    public MockSession(
            Function<String, MiniResult> resultProvider,
            PutLargeDataFunction putLargeDataFunction) {
        this.resultProvider = resultProvider;
        this.putLargeDataFunction = putLargeDataFunction;
    }
    
    
    @Override
    public MiniResult execute(String query) {
        return resultProvider.apply(query);
    }

    @Override
    public MiniLargeDataSaveResult putLargeData(
            String variableName, long length, InputStream dataSource) {
        if (putLargeDataFunction == null) {
            throw new UnsupportedOperationException();
        }
        
        return putLargeDataFunction.putLargeData(variableName, length, dataSource);
    }

    @Override
    public void close() {
        // nothing to do
    }
    
    
    @FunctionalInterface
    public interface PutLargeDataFunction {

        public MiniLargeDataSaveResult putLargeData(
                String variableName, long length, InputStream dataSource);

    }

}
