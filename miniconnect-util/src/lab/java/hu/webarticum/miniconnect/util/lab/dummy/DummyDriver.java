package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.Map;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniDriver;
import hu.webarticum.miniconnect.util.manager.MiniDriverManager;

public class DummyDriver implements MiniDriver {

    private static final String VERSION = "0.0.1";

    private static final String URL = "dummy";
    
    static {
        MiniDriverManager.register(new DummyDriver());
    }
    
    
    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public boolean canAccept(String url) {
        return url.equals(URL);
    }

    @Override
    public MiniSession openSession(String url, Map<?, ?> properties) {
        return new DummySession();
    }

}
