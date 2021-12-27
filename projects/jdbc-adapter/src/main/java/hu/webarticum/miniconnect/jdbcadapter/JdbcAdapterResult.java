package hu.webarticum.miniconnect.jdbcadapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResultSet;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class JdbcAdapterResult implements MiniResult {
    
    private static final int MAX_WARNINGS = 1000;
    
    
    private final boolean success;
    
    private final MiniError error;
    
    private final ImmutableList<MiniError> warnings;
    
    private final boolean hasResultSet;
    
    private final MiniResultSet resultSet;
    
    
    public JdbcAdapterResult(Statement jdbcStatement) {
        this(
                true,
                new StoredError(0, "00000", ""),
                extractWarnings(jdbcStatement),
                jdbcStatement,
                extractResultSet(jdbcStatement));
    }

    private static ImmutableList<MiniError> extractWarnings(Statement jdbcStatement) {
        try {
            return extractWarningsThrows(jdbcStatement);
        } catch (SQLException e) {
            return ImmutableList.empty();
        }
    }
    
    private static ImmutableList<MiniError> extractWarningsThrows(
            Statement jdbcStatement) throws SQLException {
        List<MiniError> resultBuilder = new ArrayList<>();
        for (
                SQLWarning jdbcWarning = jdbcStatement.getWarnings();
                jdbcWarning != null && resultBuilder.size() < MAX_WARNINGS;
                jdbcWarning = jdbcWarning.getNextWarning()) {
            resultBuilder.add(convertWarning(jdbcWarning));
        }
        return new ImmutableList<>(resultBuilder);
    }
    
    private static MiniError convertWarning(SQLWarning jdbcWarning) {
        return new StoredError(
                jdbcWarning.getErrorCode(),
                jdbcWarning.getSQLState(),
                jdbcWarning.getMessage());
    }

    public JdbcAdapterResult(SQLException jdbcException) {
        this(
                false,
                new StoredError(
                        jdbcException.getErrorCode(),
                        jdbcException.getSQLState(),
                        jdbcException.getMessage()),
                ImmutableList.empty(),
                null,
                null);
    }

    private JdbcAdapterResult(
            boolean success,
            StoredError error,
            ImmutableList<MiniError> warnings,
            Statement jdbcStatement,
            ResultSet jdbcResultSet) {
        this.success = success;
        this.error = error;
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
    public MiniError error() {
        return error;
    }

    @Override
    public ImmutableList<MiniError> warnings() {
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
