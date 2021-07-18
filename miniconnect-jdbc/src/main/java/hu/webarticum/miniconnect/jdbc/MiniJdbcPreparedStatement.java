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
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;

public class MiniJdbcPreparedStatement extends AbstractJdbcStatement implements PreparedStatement {
    
    private final PreparedStatementProvider preparedStatementProvider;

    private final ArrayList<ParameterValue> parameters = new ArrayList<>(4);

    private final Object closeLock = new Object();
    
    private volatile boolean closed = false;
    
    
    MiniJdbcPreparedStatement(MiniJdbcConnection connection, String sql) {
        super(connection);
        this.preparedStatementProvider =
                connection.getDatabaseProvider().prepareStatement(connection.getMiniSession(), sql);
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
    public ResultSet executeQuery() throws SQLException {
        ResultHolder resultHolder = executeInternal();
        return resultHolder.jdbcResultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        ResultHolder resultHolder = executeInternal();
        return 0; // TODO
    }

    @Override
    public boolean execute() throws SQLException {
        ResultHolder resultHolder = executeInternal();
        return resultHolder.result.hasResultSet();
    }

    private ResultHolder executeInternal() throws SQLException {
        MiniResult result = preparedStatementProvider.execute(parameters);
        if (!result.success()) {
            MiniError error = result.error();
            throw new SQLException(
                    error.message(),
                    error.sqlState(),
                    error.code());
        }
        
        MiniJdbcResultSet jdbcResultSet =
                result.hasResultSet() ?
                new MiniJdbcResultSet(this, result.resultSet()) :
                null;
        ResultHolder resultHolder = new ResultHolder(result, jdbcResultSet);
        setCurrentResult(resultHolder);
        
        return resultHolder;
    }

    // [end]
    

    // --- BATCH ---
    // [start]
    
    @Override
    public void addBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // [end]

    
    // --- SETTERS ---
    // [start]
    
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setNull(parameterIndex, sqlType, null);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Void.TYPE, null, sqlType, typeName, null));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Boolean.class, x));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Byte.class, x));
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Short.class, x));
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Integer.class, x));
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Long.class, x));
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Float.class, x));
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Double.class, x));
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(BigDecimal.class, x));
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(byte[].class, x));
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(String.class, x));
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                String.class, value, Types.NVARCHAR, null, null));
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Date.class, x));
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Date.class, x, Types.OTHER, null, cal));
    }
    
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Time.class, x));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Time.class, x, Types.OTHER, null, cal));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Timestamp.class, x));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Timestamp.class, x, Types.OTHER, null, cal));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(URL.class, x));
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(SQLXML.class, xmlObject));
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
        setObject(parameterIndex, x, Types.OTHER);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Object.class, x, targetSqlType, null, null));
    }

    @Override
    public void setObject(
            int parameterIndex, Object x, int targetSqlType, int scaleOrLength
            ) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Object.class, x, targetSqlType, null, scaleOrLength));
    }
    
    private synchronized void setParameter(int parameterIndex, ParameterValue parameter) {
        int currentSize = parameters.size();
        if (parameterIndex > currentSize) {
            parameters.ensureCapacity(parameterIndex);
            for (int i = currentSize; i < parameterIndex; i++) {
                parameters.add(null);
            }
        }
        
        parameters.set(parameterIndex - 1, parameter);
    }

    @Override
    public synchronized void clearParameters() throws SQLException {
        parameters.clear();
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
    
    
    // --- CLOSE ---
    // [start]
    
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }
    
    @Override
    public void close() throws SQLException {
        synchronized (closeLock) {
            if (!closed) {
                closeInternal();
            }
        }
    }

    // TODO
    public void closeInternal() throws SQLException {
        closed = true;
        getConnection().unregisterActiveStatement(this);
        try {
            preparedStatementProvider.close();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    
    // [end]
    
    
    private SQLException createMethodNotAllowedException() {
        return new SQLException("Method not allowed for prepared statement");
    }
    
}
