package hu.webarticum.miniconnect.tool.mock;

import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;

public class MockSessionManager implements MiniSessionManager {

    private final Function<String, MiniResult> resultSupplier;
    

    public MockSessionManager(Function<String, MiniResult> resultSupplier) {
        this.resultSupplier = resultSupplier;
    }
    
    
    @Override
    public MiniSession openSession() {
        return new MockSession(resultSupplier);
    }

}
