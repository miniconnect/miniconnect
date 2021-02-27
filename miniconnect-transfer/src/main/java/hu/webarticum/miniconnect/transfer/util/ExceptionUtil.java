package hu.webarticum.miniconnect.transfer.util;

import java.io.InterruptedIOException;

public final class ExceptionUtil {
    
    private ExceptionUtil() {
        // utility class
    }
    

    public static InterruptedIOException convertInterruption(InterruptedException e) {
        Thread.currentThread().interrupt();
        InterruptedIOException ioE = new InterruptedIOException();
        ioE.addSuppressed(e);
        return ioE;
    }
    
}
