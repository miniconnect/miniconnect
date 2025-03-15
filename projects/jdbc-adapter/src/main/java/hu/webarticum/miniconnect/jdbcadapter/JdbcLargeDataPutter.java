package hu.webarticum.miniconnect.jdbcadapter;

import java.io.InputStream;
import java.sql.Connection;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public interface JdbcLargeDataPutter {

    public MiniLargeDataSaveResult putLargeData(
            Connection jdbcConnection, String variableName, long length, InputStream dataSource);

}
