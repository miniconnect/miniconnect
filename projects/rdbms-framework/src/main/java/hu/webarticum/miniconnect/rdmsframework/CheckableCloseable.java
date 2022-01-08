package hu.webarticum.miniconnect.rdmsframework;

import java.io.Closeable;

public interface CheckableCloseable extends Closeable {

    @Override
    public void close();

    public boolean isClosed();
    
    public default void checkClosed() {
        if (isClosed()) {
            String className = this.getClass().getName();
            throw new IllegalStateException("Closed instance of " + className);
        }
    }
    
}
