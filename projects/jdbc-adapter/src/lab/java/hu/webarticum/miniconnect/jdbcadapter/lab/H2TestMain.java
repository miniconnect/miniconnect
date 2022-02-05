package hu.webarticum.miniconnect.jdbcadapter.lab;

import java.sql.SQLException;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSessionManager;
import hu.webarticum.miniconnect.jdbcadapter.JdbcLargeDataPutter;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;
import hu.webarticum.miniconnect.util.data.ByteString;

public class H2TestMain {
    
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    
    private static final String JDBC_USERNAME = "";
    
    private static final String JDBC_PASSWORD = "";
    
    private static final String SET_STATEMENT = "SET @%s = ?";
    

    public static void main(String[] args) throws Exception {
        runRepl(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, SET_STATEMENT);
    }
    
    private static void runRepl(
            String url, String username, String password, String setStatement) throws SQLException {
        System.out.println(ByteString.of("Hello!"));
        Supplier<JdbcLargeDataPutter> largeDataPutterFactory =
                () -> new SimpleJdbcLargeDataPutter(setStatement);
        MiniSessionManager sessionManager =
                new JdbcAdapterSessionManager(url, username, password, largeDataPutterFactory);
        try (MiniSession session = sessionManager.openSession()) {
            Repl repl = new SqlRepl(
                    session,
                    System.out); // NOSONAR
            new ReplRunner(repl, System.in).run();
        }
    }
    
}
