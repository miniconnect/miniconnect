package hu.webarticum.miniconnect.jdbcadapter;

import java.sql.SQLException;

public class UncheckedSqlException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    

    public UncheckedSqlException(SQLException jdbcException) {
        super(jdbcException.getMessage(), jdbcException);
    }

    @Override
    public SQLException getCause() { // NOSONAR no synchronization is required
        return (SQLException) super.getCause();
    }
}
