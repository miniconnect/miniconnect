package hu.webarticum.miniconnect.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSession;
import hu.webarticum.miniconnect.messenger.lab.dummy.DummyMessenger;

public class MiniJdbcConnectionTest {

    @Test
    void testSelect() throws Exception {
        Map<String, String> contents = new LinkedHashMap<>();
        contents.put("first", "Lorem ipsum");
        contents.put("second", "Hello World");
        contents.put("third", "Apple Banana");
        try (MiniSession session = new MessengerSession(1L, new DummyMessenger())) {
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                String name = entry.getKey();
                String content = entry.getValue();
                byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                InputStream dataSource = new ByteArrayInputStream(contentBytes);
                session.putLargeData(name, contentBytes.length, dataSource);
            }
            
            Connection connection = new MiniJdbcConnection(session);
            try (Statement selectStatement = connection.createStatement()) {
                try (ResultSet resultSet = selectStatement.getResultSet()) {
                    
                    // TODO
                    
                }
            }
        }
    }
    
}
