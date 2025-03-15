package hu.webarticum.miniconnect.impl.mock;

import java.util.function.Function;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.impl.mock.MockSession.PutLargeDataFunction;

public class MockSessionManager implements MiniSessionManager {

    private final Supplier<MiniSession> sessionFactory;
    

    public MockSessionManager(Function<String, MiniResult> resultProvider) {
        this(() -> new MockSession(resultProvider));
    }

    public MockSessionManager(
            Function<String, MiniResult> resultProvider,
            PutLargeDataFunction putLargeDataFunction) {
        this(() -> new MockSession(resultProvider, putLargeDataFunction));
    }

    public MockSessionManager(Supplier<MiniSession> sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
    @Override
    public MiniSession openSession() {
        return sessionFactory.get();
    }

}
