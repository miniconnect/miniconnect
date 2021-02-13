package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.Map;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.util.manager.Driver;
import hu.webarticum.miniconnect.util.manager.DriverManager;

public class DummyDriver implements Driver {

    private static final String VERSION = "0.0.1";

    private static final String URL = "dummy";
    
    static {
        DriverManager.register(new DummyDriver());
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
    public MiniConnection openConnection(String url, Map<?, ?> properties) {
        return new DummyConnection();
    }

}
