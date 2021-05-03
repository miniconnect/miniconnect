package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface MiniSession extends Closeable {

    public MiniResult execute(String query) throws IOException;

    public MiniLobResult putLargeData(long length, InputStream dataSource) throws IOException;

}
