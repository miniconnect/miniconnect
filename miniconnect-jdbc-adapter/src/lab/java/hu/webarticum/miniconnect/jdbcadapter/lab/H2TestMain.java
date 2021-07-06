package hu.webarticum.miniconnect.jdbcadapter.lab;

import java.sql.Connection;
import java.sql.DriverManager;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class H2TestMain {
    
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    

    public static void main(String[] args) throws Exception {
        try (Connection jdbcConnection = DriverManager.getConnection(JDBC_URL)) {
            SimpleJdbcLargeDataPutter largeDataPutter =
                    new SimpleJdbcLargeDataPutter("SET @%s = ?");
            try (MiniSession session = new JdbcAdapterSession(jdbcConnection, largeDataPutter)) {
                Repl repl = new SqlRepl(
                        session,
                        System.out); // NOSONAR
                new ReplRunner(repl, System.in).run();
            }
        }
    }
    
}
