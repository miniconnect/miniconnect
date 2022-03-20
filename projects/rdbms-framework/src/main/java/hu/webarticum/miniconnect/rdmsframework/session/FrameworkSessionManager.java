package hu.webarticum.miniconnect.rdmsframework.session;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.rdmsframework.engine.Engine;

public class FrameworkSessionManager implements MiniSessionManager {
    
    private final Engine engine;
    

    public FrameworkSessionManager(Engine engine) {
        this.engine = engine;
    }
    
    
    @Override
    public MiniSession openSession() {
        return new FrameworkSession(engine.openSession());
    }

}
