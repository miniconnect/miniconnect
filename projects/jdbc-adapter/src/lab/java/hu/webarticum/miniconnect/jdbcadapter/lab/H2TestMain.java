package hu.webarticum.miniconnect.jdbcadapter.lab;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class H2TestMain {
    
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    
    private static final String JDBC_USERNAME = "";
    
    private static final String JDBC_PASSWORD = "";
    
    private static final String SET_STATEMENT = "SET @%s = ?";
    

    public static void main(String[] args) throws Exception {
        runRepl(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, SET_STATEMENT);
    }
    
    private static void runRepl(
            String url, String username, String password, String setStatement
            ) throws SQLException, IOException {
        try (Connection jdbcConnection = DriverManager.getConnection(url, username, password)) {
            SimpleJdbcLargeDataPutter largeDataPutter =
                    new SimpleJdbcLargeDataPutter(setStatement);
            try (MiniSession session = new JdbcAdapterSession(jdbcConnection, largeDataPutter)) {
                Repl repl = new SqlRepl(
                        session,
                        System.out); // NOSONAR
                new ReplRunner(repl, System.in).run();
            }
        }
    }
    
}
