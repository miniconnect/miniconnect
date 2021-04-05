package hu.webarticum.miniconnect.server.surface;

import java.io.Closeable;
import java.io.IOException;

public interface CloseableSource<T> extends Closeable {

    public T fetch() throws IOException;
    
}
