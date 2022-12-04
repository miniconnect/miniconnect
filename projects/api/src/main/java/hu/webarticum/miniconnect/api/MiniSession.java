package hu.webarticum.miniconnect.api;

import java.io.Closeable;
import java.io.InputStream;

public interface MiniSession extends Closeable {

    public MiniResult execute(String query);

    public MiniLargeDataSaveResult putLargeData(String variableName, long length, InputStream dataSource);

    @Override
    public void close();

}
