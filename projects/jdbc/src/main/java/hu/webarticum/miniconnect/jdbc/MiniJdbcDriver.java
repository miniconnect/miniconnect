package hu.webarticum.miniconnect.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.h2.H2DatabaseProvider;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.server.ClientMessenger;

public class MiniJdbcDriver implements Driver {

    public static final int JDBC_MAJOR_VERSION = 4;
    
    public static final int JDBC_MINOR_VERSION = 2;
    
    public static final String DRIVER_NAME = "MiniConnect JDBC";
    
    public static final int DRIVER_MAJOR_VERSION = 0;
    
    public static final int DRIVER_MINOR_VERSION = 1;
    
    public static final String DRIVER_VERSION = DRIVER_MAJOR_VERSION + "." + DRIVER_MINOR_VERSION;
    
    public static final String PROPERTY_USER = "user";
    
    public static final String PROPERTY_PASSSWORD = "password";
    
    public static final String PROPERTY_PROVIDER = "provider";
    
    public static final String PROVIDER_MINIBASE = "minibase";
    
    public static final String PROVIDER_H2 = "h2";
    
    
    @Override
    public boolean acceptsURL(String url) {
        return ConnectionUrlInfo.isUrlSupported(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return DRIVER_MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return DRIVER_MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        ConnectionUrlInfo urlInfo = ConnectionUrlInfo.parse(url, info);
        ImmutableMap<String, String> properties = urlInfo.properties();
        String schema = urlInfo.schema();
        
        ClientMessenger clientMessenger = new ClientMessenger(urlInfo.host(), urlInfo.port());
        MiniSession session = new MessengerSessionManager(clientMessenger).openSession();
        String providerName = properties.getOrDefault(PROPERTY_PROVIDER, PROVIDER_H2);
        DatabaseProvider databaseProvider = createProviderFor(providerName);
            
        Connection connection = new MiniJdbcConnection(session, databaseProvider, url, clientMessenger::close);
        if (schema != null) {
            try {
                connection.setSchema(schema);
            } catch (Exception e) {
                connection.close();
                throw e;
            }
        }
        
        return connection;
    }
    
    private DatabaseProvider createProviderFor(String providerName) {
        // FIXME
        if (providerName.equals(PROVIDER_MINIBASE)) {
            
            // TODO
            throw new UnsupportedOperationException("Not implemented yet");
            
        } else if (providerName.equals(PROVIDER_H2)) {
            return new H2DatabaseProvider();
        } else {
            throw new IllegalArgumentException("Unsupported provider name: " + providerName);
        }
    }

}
