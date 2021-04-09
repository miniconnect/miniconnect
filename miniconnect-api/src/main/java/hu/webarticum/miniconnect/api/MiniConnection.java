package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.IOException;

public interface MiniConnection extends Closeable {

    public MiniSession openSession() throws IOException;

}
