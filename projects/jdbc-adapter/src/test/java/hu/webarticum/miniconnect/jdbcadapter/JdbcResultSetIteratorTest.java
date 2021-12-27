package hu.webarticum.miniconnect.jdbcadapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JdbcResultSetIteratorTest {
    
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    
    private static final String JDBC_USERNAME = "";
    
    private static final String JDBC_PASSWORD = "";
    

    private Connection jdbcConnection;


    @Test
    void testExecute() throws Exception {
        try (Statement statement = jdbcConnection.createStatement()) {
            statement.execute("CREATE TABLE data (label TEXT)");
            statement.execute("INSERT INTO data (label) VALUES ('lorem')");
            statement.execute("INSERT INTO data (label) VALUES ('ipsum')");
            statement.execute("INSERT INTO data (label) VALUES ('hello')");
            statement.execute("INSERT INTO data (label) VALUES ('world')");
        }

        List<String> fetchedLabels = new ArrayList<>();
        String query = "SELECT label FROM data";
        try (
                Statement queryStatement = jdbcConnection.createStatement();
                ResultSet resultSet = queryStatement.executeQuery(query)) {
            Iterator<String> iterator = new JdbcResultSetIterator<String>(
                    resultSet, r -> r.getString(1));
            iterator.forEachRemaining(label -> fetchedLabels.add(label));
        }

        assertThat(fetchedLabels).containsExactly("lorem", "ipsum", "hello", "world");
    }

    @BeforeEach
    void init() throws SQLException {
        jdbcConnection = DriverManager.getConnection(
                JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
    }

    @AfterEach
    void uninit() throws IOException, SQLException {
        jdbcConnection.close();
    }

}
