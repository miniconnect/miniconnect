package hu.webarticum.miniconnect.jdbcadapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;

public class JdbcAdapterSessionManager implements MiniSessionManager {
    
    private final ConnectionFactory connectionFactory;
    
    private final Supplier<JdbcLargeDataPutter> largeDataPutterFactory;
    

    public JdbcAdapterSessionManager(String connectionUrl) {
        this(() -> DriverManager.getConnection(connectionUrl));
    }

    public JdbcAdapterSessionManager(
            String connectionUrl,
            Supplier<JdbcLargeDataPutter> largeDataPutterFactory) {
        this(() -> DriverManager.getConnection(connectionUrl), largeDataPutterFactory);
    }

    public JdbcAdapterSessionManager(String connectionUrl, String username, String password) {
        this(() -> DriverManager.getConnection(connectionUrl, username, password));
    }

    public JdbcAdapterSessionManager(
            String connectionUrl,
            String username,
            String password,
            Supplier<JdbcLargeDataPutter> largeDataPutterFactory) {
        this(() -> DriverManager.getConnection(
                connectionUrl, username, password), largeDataPutterFactory);
    }

    public JdbcAdapterSessionManager(String connectionUrl, Map<?, ?> properties) {
        this(connectionFactoryOf(connectionUrl, properties));
    }

    public JdbcAdapterSessionManager(
            String connectionUrl,
            Map<?, ?> properties,
            Supplier<JdbcLargeDataPutter> largeDataPutterFactory) {
        this(connectionFactoryOf(connectionUrl, properties), largeDataPutterFactory);
    }
    
    public JdbcAdapterSessionManager(ConnectionFactory connectionFactory) {
        this(connectionFactory, null);
    }

    public JdbcAdapterSessionManager(
            ConnectionFactory connectionFactory,
            Supplier<JdbcLargeDataPutter> largeDataPutterFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.largeDataPutterFactory =
                Objects.requireNonNullElse(largeDataPutterFactory, () -> null);
    }
    
    private static ConnectionFactory connectionFactoryOf(String connectionUrl, Map<?, ?> data) {
        Properties properties = new Properties(data.size());
        properties.putAll(data);
        return () -> DriverManager.getConnection(connectionUrl, properties);
    }
    
    
    @Override
    public MiniSession openSession() {
        Connection connection;
        try {
            connection = connectionFactory.openConnection();
        } catch (SQLException e) {
            
            // FIXME
            e.printStackTrace();
            
            throw new UncheckedSqlException(e);
        }
        
        System.out.println("connection: " + connection);
        
        JdbcLargeDataPutter largeDataPutter = largeDataPutterFactory.get();
        return new JdbcAdapterSession(connection, largeDataPutter);
    }
    
    
    @FunctionalInterface
    public interface ConnectionFactory {
        
        public Connection openConnection() throws SQLException;
        
    }

}
