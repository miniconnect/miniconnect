package hu.webarticum.miniconnect.util.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import hu.webarticum.miniconnect.api.MiniConnection;

public class DriverManager {

    private static final List<Driver> drivers = new CopyOnWriteArrayList<>();
    
    
    private DriverManager() {
        // static class
    }
    

    public static void register(Driver driver) {
        drivers.add(driver);
    }

    public static void unregister(Driver driver) {
        drivers.remove(driver);
    }
    
    public static MiniConnection openConnection(String url, Map<?, ?> properties) throws IOException {
        for (Driver driver : drivers) {
            if (driver.canAccept(url)) {
                return driver.openConnection(url, properties);
            }
        }
        throw new NoSuchElementException("No suitable driver found");
    }
    
}
