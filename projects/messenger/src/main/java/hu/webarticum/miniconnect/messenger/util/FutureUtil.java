package hu.webarticum.miniconnect.messenger.util;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class FutureUtil {
    
    private FutureUtil() {
        // utility class
    }
    

    public static <T> Optional<T> getSilently(Future<T> future, long timeout, TimeUnit unit) {
        try {
            return Optional.ofNullable(future.get(timeout, unit));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
}
