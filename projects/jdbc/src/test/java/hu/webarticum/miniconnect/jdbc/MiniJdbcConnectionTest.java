package hu.webarticum.miniconnect.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.mock.MockSessionManager;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.impl.result.StoredValue;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.jdbc.provider.h2.H2DatabaseProvider;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

class MiniJdbcConnectionTest {
    
    @Test
    void testResultSetWithDummyMessenger() throws Exception {
        MiniSessionManager sessionManager = new MockSessionManager(this::mockResult);
        try (
                Connection connection = new MiniJdbcConnection(sessionManager.openSession(), null);
                Statement selectStatement = connection.createStatement()) {
            
            boolean executeResult = selectStatement.execute("SELECT * FROM data");
            assertThat(executeResult).isTrue();
            
            try (ResultSet resultSet = selectStatement.getResultSet()) {
                assertThat(resultSet.findColumn("id")).isEqualTo(1);
                assertThat(resultSet.findColumn("label")).isEqualTo(2);
                assertThat(resultSet.findColumn("description")).isEqualTo(3);
                assertThatThrownBy(() -> resultSet.findColumn("xxxxx"))
                        .isInstanceOf(SQLException.class);
                
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                assertThat(resultSetMetaData.getColumnCount()).isEqualTo(3);
                assertThat(resultSetMetaData.getColumnLabel(1)).isEqualTo("id");
                assertThat(resultSetMetaData.getColumnLabel(2)).isEqualTo("label");
                assertThat(resultSetMetaData.getColumnLabel(3)).isEqualTo("description");
                
                boolean next1Result = resultSet.next();
                assertThat(next1Result).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
                assertThat(resultSet.getString(2)).isEqualTo("first");
                assertThat(resultSet.getString(3)).isEqualTo("hello world");

                boolean next2Result = resultSet.next();
                assertThat(next2Result).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(2);
                assertThat(resultSet.getString(2)).isEqualTo("second");
                assertThat(resultSet.getString(3)).isEqualTo("lorem ipsum");
                
                boolean next3Result = resultSet.next();
                assertThat(next3Result).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(3);
                assertThat(resultSet.getString(2)).isEqualTo("third");
                assertThat(resultSet.getString(3)).isEqualTo("xxx yyy");
                
                boolean next4Result = resultSet.next();
                assertThat(next4Result).isFalse();
            }
        }
    }
    
    private MiniResult mockResult(String sql) {
        MiniValueDefinition intDefinition = new StoredValueDefinition(
                StandardValueType.INT.name());
        MiniValueDefinition stringDefinition = new StoredValueDefinition(
                StandardValueType.STRING.name());
        List<MiniColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new StoredColumnHeader("id", false, intDefinition));
        columnHeaders.add(new StoredColumnHeader("label", false, stringDefinition));
        columnHeaders.add(new StoredColumnHeader("description", false, stringDefinition));
        List<List<MiniValue>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                asMiniValue(1),
                asMiniValue("first"),
                asMiniValue("hello world")));
        rows.add(Arrays.asList(
                asMiniValue(2),
                asMiniValue("second"),
                asMiniValue("lorem ipsum")));
        rows.add(Arrays.asList(
                asMiniValue(3),
                asMiniValue("third"),
                asMiniValue("xxx yyy")));
        return new StoredResult(new StoredResultSetData(columnHeaders, rows));
    }
    
    private MiniValue asMiniValue(Object value) {
        return new StoredValue(asByteString(value));
    }
    
    private ByteString asByteString(Object value) {
        if (value instanceof Integer) {
            return ByteString.ofInt((int) value);
        } else if (value instanceof String) {
            return ByteString.of((String) value);
        } else {
            return ByteString.empty();
        }
    }

    @Test
    void testPreparedStatement() throws Exception {
        try (
                Connection baseConnection = createInMemoryConnection();
                MiniSession miniSession = new JdbcAdapterSession(baseConnection);
                Connection connection = new MiniJdbcConnection(
                        miniSession, new H2DatabaseProvider());
                ) {
            try (Statement createStatement = connection.createStatement()) {
                createStatement.execute("CREATE TABLE data (id INT NOT NULL, label TEXT)");
            }
            try (PreparedStatement insertPreparedStatement = connection.prepareStatement(
                    "INSERT INTO data (id, label) VALUES (?, ?)")) {
                insertPreparedStatement.setInt(1, 1);
                insertPreparedStatement.setString(2, "lorem");
                insertPreparedStatement.execute();
                insertPreparedStatement.setInt(1, 3);
                insertPreparedStatement.setString(2, "ipsum");
                insertPreparedStatement.execute();
                insertPreparedStatement.setInt(1, 7);
                insertPreparedStatement.setString(2, "uuu");
                insertPreparedStatement.execute();
            }
            try (PreparedStatement selectPreparedStatement = connection.prepareStatement(
                    "SELECT label FROM data WHERE id = ?")) {
                
                selectPreparedStatement.setInt(1, 1);
                try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getString(1)).isEqualTo("lorem");
                }

                selectPreparedStatement.setInt(1, 2);
                try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
                    assertThat(resultSet.next()).isFalse();
                }

                selectPreparedStatement.setInt(1, 3);
                try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getClob(1).getSubString(1L, 5)).isEqualTo("ipsum");
                }

            }
        }
    }

    private Connection createInMemoryConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    }

}
