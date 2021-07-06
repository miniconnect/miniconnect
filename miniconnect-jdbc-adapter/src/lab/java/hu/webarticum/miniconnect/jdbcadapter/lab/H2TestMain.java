package hu.webarticum.miniconnect.jdbcadapter.lab;

import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterResultSet;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;
import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;
import hu.webarticum.miniconnect.util.data.ImmutableList;

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
            

            /*
            PreparedStatement preparedUpdateStatement = jdbcConnection.prepareStatement("SET @alma = ?");
            PreparedStatement preparedSelectStatement = jdbcConnection.prepareStatement("SELECT @alma AS alma");
            
            Clob clob = jdbcConnection.createClob();
            String token = "ALMAKORTE123456789";
            try (Writer writer = clob.setCharacterStream(1)) {
                for (long k = 0; k < 10000; k++) {
                    writer.write(token);
                }
            }
            preparedUpdateStatement.setClob(1, clob);
            preparedUpdateStatement.execute();
            
            preparedSelectStatement.execute();
            ResultSet resultSet = preparedSelectStatement.getResultSet();
            resultSet.next();
            String part = resultSet.getClob(1).getSubString(7455, 10);
            System.out.println("part: " + part);
            
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE hello (id BIGINT AUTO_INCREMENT, content TEXT)");
                statement.execute("INSERT INTO hello (content) VALUES ('Hello, World')");
                statement.execute("INSERT INTO hello (content) VALUES ('apple')");
                statement.execute("INSERT INTO hello (content) VALUES ('banana')");
                statement.execute("INSERT INTO hello (content) VALUES ('lorem ipsum')");
                
                statement.execute("SELECT * FROM hello");
                try (ResultSet resultSet = statement.getResultSet()) {
                    try (JdbcAdapterResultSet adapter = new JdbcAdapterResultSet(resultSet)) {
                        ImmutableList<MiniColumnHeader> headers = adapter.columnHeaders();
                        int columnCount = headers.size();
                        ImmutableList<DefaultValueInterpreter> interpreters = headers.map(
                                h -> new DefaultValueInterpreter(h.valueDefinition()));
                        ImmutableList<MiniValue> row;
                        while ((row = adapter.fetch()) != null) {
                            System.out.println();
                            for (int i = 0; i < columnCount; i++) {
                                String columnName = headers.get(i).name();
                                Object decodedValue = interpreters.get(i).decode(row.get(i));
                                System.out.println(String.format(
                                        "%s: %s (%s)",
                                        columnName, decodedValue, decodedValue.getClass()));
                            }
                        }
                    }
                }
            }*/
        }
    }
    
}
