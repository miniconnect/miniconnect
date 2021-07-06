package hu.webarticum.miniconnect.jdbcadapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.tool.result.StoredResultSet;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class JdbcAdapterResult implements MiniResult {
    
    private final boolean success;
    
    private final String errorCode;
    
    private final String sqlState;
    
    private final String errorMessage;
    
    private final ImmutableList<String> warnings;
    
    private final boolean hasResultSet;
    
    private final MiniResultSet resultSet;
    
    
    public JdbcAdapterResult(Statement jdbcStatement) {
        this(
                true,
                "0",
                "00000",
                "",
                ImmutableList.empty(), // FIXME
                jdbcStatement,
                extractResultSet(jdbcStatement));
    }

    public JdbcAdapterResult(SQLException jdbcException) {
        this(
                false,
                "" + jdbcException.getErrorCode(),
                jdbcException.getSQLState(),
                jdbcException.getMessage(),
                ImmutableList.empty(), // FIXME
                null,
                null);
    }

    private JdbcAdapterResult(
            boolean success,
            String errorCode,
            String sqlState,
            String errorMessage,
            ImmutableList<String> warnings,
            Statement jdbcStatement,
            ResultSet jdbcResultSet) {
        this.success = success;
        this.errorCode = errorCode;
        this.sqlState = sqlState;
        this.errorMessage = errorMessage;
        this.warnings = warnings;
        this.hasResultSet = (jdbcResultSet != null);
        this.resultSet = asMiniResultSet(jdbcStatement, jdbcResultSet);
    }
    
    private static ResultSet extractResultSet(Statement jdbcStatement) {
        try {
            return jdbcStatement.getResultSet();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private static MiniResultSet asMiniResultSet(Statement jdbcStatement, ResultSet jdbcResultSet) {
        if (jdbcResultSet == null) {
            return new StoredResultSet();
        }
        
        return new JdbcAdapterResultSet(jdbcStatement, jdbcResultSet);
    }
    

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String sqlState() {
        return sqlState;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public ImmutableList<String> warnings() {
        return warnings;
    }

    @Override
    public boolean hasResultSet() {
        return hasResultSet;
    }

    @Override
    public MiniResultSet resultSet() {
        return resultSet;
    }

}
