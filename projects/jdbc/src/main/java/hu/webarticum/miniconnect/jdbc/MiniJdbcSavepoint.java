package hu.webarticum.miniconnect.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public class MiniJdbcSavepoint implements Savepoint {
    
    private final int id;
    
    private final String name;
    

    public MiniJdbcSavepoint(int id) {
        this.id = id;
        this.name = null;
    }
    
    public MiniJdbcSavepoint(String name) {
        this.id = 0;
        this.name = name;
    }
    

    public boolean isNamed() {
        return name != null;
    }
    
    @Override
    public int getSavepointId() throws SQLException {
        if (name != null) {
            throw new SQLException("This is a named savepoint");
        }
        
        return id;
    }

    @Override
    public String getSavepointName() throws SQLException {
        if (name == null) {
            throw new SQLException("This is an unnamed savepoint");
        }
        
        return name;
    }

}
