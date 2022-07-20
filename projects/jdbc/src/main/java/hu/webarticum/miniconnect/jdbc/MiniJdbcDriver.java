package hu.webarticum.miniconnect.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class MiniJdbcDriver implements Driver {

    public static final int JDBC_MAJOR_VERSION = 4;
    
    public static final int JDBC_MINOR_VERSION = 2;
    
    public static final String DRIVER_NAME = "MiniConnect JDBC";
    
    public static final int DRIVER_MAJOR_VERSION = 0;
    
    public static final int DRIVER_MINOR_VERSION = 1;
    
    public static final String DRIVER_VERSION = DRIVER_MAJOR_VERSION + "." + DRIVER_MINOR_VERSION;
    
    
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
        
        // TODO
        
        throw new UnsupportedOperationException("not implemented yet, urlInfo: " + urlInfo);
        
    }

}
