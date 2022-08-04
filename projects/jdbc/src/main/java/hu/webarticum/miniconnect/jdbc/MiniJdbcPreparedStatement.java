package hu.webarticum.miniconnect.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
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
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.jdbc.blob.BlobClob;
import hu.webarticum.miniconnect.jdbc.blob.WriteableBlob;
import hu.webarticum.miniconnect.jdbc.io.LongBoundedReader;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;

public class MiniJdbcPreparedStatement extends AbstractJdbcStatement implements PreparedStatement {
    
    private final PreparedStatementProvider preparedStatementProvider;

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
        ResultSet resultSet = getResultSet();
        if (resultSet == null) {
            return null; // FIXME try to parse?
        }
        
        return resultSet.getMetaData();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return new MiniJdbcParameterMetaData(preparedStatementProvider.parameters());
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // not supported
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
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
        executeInternal();
        return 1; // FIXME / TODO: this is an ugly heuristic
    }

    @Override
    public boolean execute() throws SQLException {
        ResultHolder resultHolder = executeInternal();
        return resultHolder.result.hasResultSet();
    }

    private ResultHolder executeInternal() throws SQLException {
        MiniResult result = preparedStatementProvider.execute();
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
        handleExecuteCompleted(preparedStatementProvider.sql(), resultHolder);
        
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
        setParameter(parameterIndex, new ParameterValue(Void.TYPE, null, sqlType, typeName, null));
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
        setParameter(parameterIndex, new ParameterValue(String.class, value, Types.NVARCHAR, null, null));
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Date.class, x));
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Date.class, x, Types.OTHER, null, cal));
    }
    
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Time.class, x));
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Time.class, x, Types.OTHER, null, cal));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Timestamp.class, x));
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Timestamp.class, x, Types.OTHER, null, cal));
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(URL.class, x));
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setAsciiStream(parameterIndex, x, (long) length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        setClob(parameterIndex, reader);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        setCharacterStream(parameterIndex, reader, (long) length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        setNCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        setNClob(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Reader.class, value, length));
    }

    @Deprecated
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setBlob(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setBinaryStream(parameterIndex, x, (long) length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(InputStream.class, x, length));
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Blob.class, x));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Blob.class, blobOf(inputStream), Types.OTHER, null, null, true));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Blob.class, blobOf(inputStream, length), Types.OTHER, null, null, true));
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Clob.class, x));
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Clob.class, clobOf(reader), Types.OTHER, null, null, true));
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                Clob.class, clobOf(reader, length), Types.OTHER, null, null, true));
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(NClob.class, value));
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(NClob.class, clobOf(reader), Types.OTHER, null, null, true));
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(
                NClob.class, clobOf(reader, length), Types.OTHER, null, null, true));
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        setObject(parameterIndex, x, Types.OTHER);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Object.class, x, targetSqlType, null, null));
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setParameter(parameterIndex, new ParameterValue(Object.class, x, targetSqlType, null, scaleOrLength));
    }
    
    private synchronized void setParameter(int parameterIndex, ParameterValue parameter) {
        int zeroBasedIndex = parameterIndex - 1;
        preparedStatementProvider.setParameterValue(zeroBasedIndex, parameter);
    }

    @Override
    public synchronized void clearParameters() throws SQLException {
        preparedStatementProvider.clearParameterValues();
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

    public void closeInternal() throws SQLException {
        closed = true;
        getConnection().unregisterActiveStatement(this);
        Exception resultSetCloseException = null;
        try {
            ResultSet resultSet = getResultSet();
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
            resultSetCloseException = e;
        }
        try {
            preparedStatementProvider.close();
        } catch (Exception e) {
            SQLException e2 = new SQLException(e);
            if (resultSetCloseException != null) {
                e2.addSuppressed(resultSetCloseException);
            }
            throw e2;
        }
        if (resultSetCloseException != null) {
            if (resultSetCloseException instanceof SQLException) {
                throw (SQLException) resultSetCloseException;
            } else {
                throw new SQLException(resultSetCloseException);
            }
        }
    }
    
    // [end]
    

    private Blob blobOf(InputStream inputStream, long length) throws SQLException {
        return blobOf(new BoundedInputStream(inputStream, length));
    }
    
    private Blob blobOf(InputStream inputStream) throws SQLException {
        WriteableBlob blob = new WriteableBlob();
        OutputStream blobOutputStream = blob.setBinaryStream(1);
        try {
            IOUtils.copy(inputStream, blobOutputStream);
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return blob;
    }

    private NClob clobOf(Reader reader, long length) throws SQLException {
        return clobOf(new LongBoundedReader(reader, length));
    }
    
    private NClob clobOf(Reader reader) throws SQLException {
        BlobClob clob = getConnection().createUtf8BlobClob();
        Writer clobWriter = clob.setCharacterStream(1);
        try {
            IOUtils.copy(reader, clobWriter);
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return clob;
    }

    private SQLException createMethodNotAllowedException() {
        return new SQLException("Method not allowed for prepared statement");
    }
    
}
