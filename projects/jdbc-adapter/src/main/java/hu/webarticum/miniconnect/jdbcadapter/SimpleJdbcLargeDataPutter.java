package hu.webarticum.miniconnect.jdbcadapter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredLargeDataSaveResult;

public class SimpleJdbcLargeDataPutter implements JdbcLargeDataPutter {
    
    private static final String DEFAULT_SQL_STATE = "00000";
    
    
    private final String setterStatementFormat;
    
    
    public SimpleJdbcLargeDataPutter(String setterStatementFormat) {
        this.setterStatementFormat = setterStatementFormat;
    }
    
    
    @Override
    public MiniLargeDataSaveResult putLargeData(
            Connection jdbcConnection, String variableName, long length, InputStream dataSource) {
        try (
                PreparedStatement preparedStatement =
                jdbcConnection.prepareStatement(String.format(setterStatementFormat, variableName))
                ) {
            preparedStatement.setBinaryStream(1, dataSource, length);
            preparedStatement.execute();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            String sqlState = e.getSQLState();
            String errorMessage = e.getMessage();
            return new StoredLargeDataSaveResult(
                    false,
                    new StoredError(errorCode, sqlState, errorMessage));
        }
        return new StoredLargeDataSaveResult(
                true,
                new StoredError(0, DEFAULT_SQL_STATE, ""));
    }

}
