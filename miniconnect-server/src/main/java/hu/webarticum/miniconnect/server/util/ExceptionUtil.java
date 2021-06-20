package hu.webarticum.miniconnect.server.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;

public final class ExceptionUtil {
    
    private ExceptionUtil() {
        // utility class
    }
    
    
    public static RuntimeException asUncheckedIOException(Exception exception) {
        IOException ioException = new InterruptedIOException();
        ioException.addSuppressed(exception);
        return new UncheckedIOException(ioException);
    }
    
}
