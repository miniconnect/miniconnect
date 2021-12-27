package hu.webarticum.miniconnect.transfer.util;

public final class ExceptionUtil {
    
    private ExceptionUtil() {
        // utility class
    }
    

    public static <E extends Throwable> E combine(E targetException, Throwable originalException) {
        targetException.addSuppressed(originalException);
        return targetException;
    }
    
}
