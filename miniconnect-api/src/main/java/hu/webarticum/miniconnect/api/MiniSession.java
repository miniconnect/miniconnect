package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface MiniSession extends Closeable {

    public MiniResult execute(String query) throws IOException;

    // FIXME: MiniLobResult?
    public String putLargeData(InputStream dataSource) throws IOException;

}
