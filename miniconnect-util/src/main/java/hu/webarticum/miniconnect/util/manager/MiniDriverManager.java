package hu.webarticum.miniconnect.util.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniDriver;

public class MiniDriverManager {

    private static final List<MiniDriver> drivers = new CopyOnWriteArrayList<>();
    
    
    private MiniDriverManager() {
        // static class
    }
    

    public static void register(MiniDriver driver) {
        drivers.add(driver);
    }

    public static void unregister(MiniDriver driver) {
        drivers.remove(driver);
    }
    
    public static MiniConnection openConnection(String url, Map<?, ?> properties) throws IOException {
        for (MiniDriver driver : drivers) {
            if (driver.canAccept(url)) {
                return driver.openConnection(url, properties);
            }
        }
        throw new NoSuchElementException("No suitable driver found");
    }
    
}
