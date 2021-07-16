package hu.webarticum.miniconnect.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class MiniJdbcPreparedStatement extends AbstractJdbcStatement implements PreparedStatement {

    MiniJdbcPreparedStatement(MiniJdbcConnection connection) {
        super(connection);
        
    }
    

    // --- METADATA ---
    // [start]
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null; // TODO
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null; // TODO
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // TODO
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false; // TODO
    }

    // [end]
    

    // --- EXECUTE ---
    // [start]
    
    @Override
    public boolean execute() throws SQLException {
        return false; // TODO
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return null; // TODO
    }

    @Override
    public int executeUpdate() throws SQLException {
        return 0; // TODO
    }

    // [end]
    

    // --- BATCH ---
    // [start]
    
    @Override
    public void addBatch() throws SQLException {
        // TODO
    }

    @Override
    public void clearBatch() throws SQLException {
        // TODO
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return null; // TODO
    }

    // [end]

    
    // --- SETTERS ---
    // [start]
    
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        // TODO
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        // TODO
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        // TODO
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        // TODO
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        // TODO
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        // TODO
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        // TODO
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        // TODO
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        // TODO
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        // TODO
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        // TODO
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        // TODO
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        // TODO
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        // TODO
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        // TODO
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        // TODO
    }
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        // TODO
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        // TODO
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        // TODO
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        // TODO
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        // TODO
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        // TODO
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        // TODO
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // TODO
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        // TODO
    }

    @Override
    public void setCharacterStream(
            int parameterIndex, Reader reader, int length) throws SQLException {
        // TODO
    }

    @Override
    public void setCharacterStream(
            int parameterIndex, Reader reader, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        // TODO
    }

    @Override
    public void setNCharacterStream(
            int parameterIndex, Reader value, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setUnicodeStream(
            int parameterIndex, InputStream x, int length) throws SQLException {
        // TODO
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        // TODO
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // TODO
    }

    @Override
    public void setBinaryStream(
            int parameterIndex, InputStream x, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        // TODO
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        // TODO
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        // TODO
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        // TODO
    }

    @Override
    public void setBlob(
            int parameterIndex, InputStream inputStream, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        // TODO
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // TODO
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        // TODO
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        // TODO
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // TODO
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        // TODO
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        // TODO
    }

    @Override
    public void setObject(
            int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        // TODO
    }

    @Override
    public void clearParameters() throws SQLException {
        // TODO
    }

    // [end]
    

    // --- NON PREPARED METHODS ---
    // [start]
    
    @Override
    public boolean execute(String sql) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw createMethodNotAllowedException();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw createMethodNotAllowedException();
    }

    // [end]
    
    
    private SQLException createMethodNotAllowedException() {
        return new SQLException("Method not allowed for prepared statement");
    }
    
}
