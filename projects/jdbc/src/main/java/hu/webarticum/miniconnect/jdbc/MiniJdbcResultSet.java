package hu.webarticum.miniconnect.jdbc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.jdbc.blob.BlobClob;
import hu.webarticum.miniconnect.jdbc.blob.ContentAccessBlob;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.ResultField;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public class MiniJdbcResultSet implements ResultSet {
    
    private final Statement statement;
    
    private final ResultTable resultTable;
    
    private final MiniJdbcResultSetMetaData metaData;
    
    private final int columnCount;
    
    
    private volatile ResultRecord currentRecord = null; // NOSONAR
    
    private volatile boolean wasNull = false;
    
    private volatile int fetchSize = 0; // XXX ignored
    

    public MiniJdbcResultSet(Statement statement, MiniResultSet miniResultSet) {
        this.statement = statement;
        this.resultTable = new ResultTable(miniResultSet);
        this.metaData = new MiniJdbcResultSetMetaData(this);
        this.columnCount = miniResultSet.columnHeaders().size();
    }
    
    
    // --- METADATA ---
    // [start]

    public ImmutableList<ValueTranslator> getValueTranslators() {
        return resultTable.valueTranslators();
    }

    public ImmutableList<MiniColumnHeader> getMiniColumnHeaders() {
        return resultTable.resultSet().columnHeaders();
    }

    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        if (!isWrapperFor(type)) {
            throw new SQLException(String.format("Unable to convert %s to %s", getClass(), type));
        }
        
        @SuppressWarnings("unchecked")
        T result = (T) this;
        return result;
    }

    @Override
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        return (type != null && type.isAssignableFrom(getClass()));
    }

    @Override
    public MiniJdbcResultSetMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // nothing to do
    }

    @Override
    public String getCursorName() throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        ImmutableList<MiniColumnHeader> columnHeaders = getMiniColumnHeaders();
        for (int i = 0; i < columnCount; i++) {
            String columnName = columnHeaders.get(i).name();
            if (columnName.equals(columnLabel)) {
                return i + 1;
            }
        }
        
        throw new SQLException(String.format("No column with label '%s'", columnLabel));
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    // [end]
    
    
    // --- SETTINGS --
    // [start]

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD) {
            throw createForwardOnlyException();
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return fetchSize;
    }

    // [end]
    

    // --- NAVIGATION ---
    // [start]

    @Override
    public int getRow() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean isLast() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public void afterLast() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean first() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean last() throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw createForwardOnlyException();
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        if (rows == 1) {
            return next();
        }
        
        throw createForwardOnlyException();
    }

    @Override
    public boolean next() throws SQLException {
        Iterator<ResultRecord> iterator = resultTable.iterator();
        if (!iterator.hasNext()) {
            currentRecord = null;
            return false;
        }
        
        currentRecord = iterator.next();
        return true;
    }

    @Override
    public boolean previous() throws SQLException {
        throw createForwardOnlyException();
    }
    
    // [end]
    
    
    // --- GET ---
    // [start]

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Boolean.class), false);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Byte.class), (byte) 0);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Short.class), (short) 0);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Integer.class), 0);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Long.class), 0L);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Float.class), 0f);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return Objects.requireNonNullElse(getObject(columnIndex, Double.class), 0.0);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal( // NOSONAR: deprecated by JDBC
            String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getObject(columnIndex, BigDecimal.class);
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal( // NOSONAR: deprecated by JDBC
            int columnIndex, int scale) throws SQLException {
        return getBigDecimal(columnIndex).setScale(scale, RoundingMode.HALF_UP);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return getObject(columnIndex, byte[].class);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getObject(columnIndex, String.class);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return getObject(columnIndex, String.class);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel, Calendar calendar) throws SQLException {
        return getDate(findColumn(columnLabel), calendar);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return getObject(columnIndex, Date.class);
    }

    @Override
    public Date getDate(int columnIndex, Calendar calendar) throws SQLException {
        Instant instant = getDate(columnIndex).toInstant();
        ZoneId zoneId = calendar.getTimeZone().toZoneId();
        return Date.valueOf(LocalDate.ofInstant(instant, zoneId));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel, Calendar calendar) throws SQLException {
        return getTime(findColumn(columnLabel), calendar);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return getObject(columnIndex, Time.class);
    }

    @Override
    public Time getTime(int columnIndex, Calendar calendar) throws SQLException {
        // FIXME: calendar?
        return getTime(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getObject(columnIndex, Timestamp.class);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar calendar) throws SQLException {
        // FIXME: calendar?
        return getTimestamp(columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return getObject(columnIndex, URL.class);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return getBinaryStream(columnIndex); // XXX conversion?
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return getNCharacterStream(columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, Reader.class);
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream( // NOSONAR: deprecated by JDBC
            String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream( // NOSONAR: deprecated by JDBC
            int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getObject(columnIndex, InputStream.class);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return getObject(columnIndex, Blob.class);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return getNClob(columnLabel);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return getNClob(columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(findColumn(columnLabel));
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return getObject(columnIndex, NClob.class);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        T streamResult = tryConvertStream(columnIndex, type);
        if (streamResult != null) {
            return streamResult;
        }
        
        ResultField resultField = getResultField(columnIndex);
        return resultField.as(type);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T tryConvertStream(int columnIndex, Class<T> type) throws SQLException {
        // FIXME: character sets?
        if (type == InputStream.class) {
            return (T) getMiniValue(columnIndex).contentAccess().inputStream();
        } else if (type == Reader.class) {
            return (T) new InputStreamReader(
                    getMiniValue(columnIndex).contentAccess().inputStream(),
                    StandardCharsets.UTF_8);
        } else if (type == Blob.class) {
            return (T) new ContentAccessBlob(getMiniValue(columnIndex).contentAccess());
        } else if (type == Clob.class || type == NClob.class) {
            return (T) new BlobClob(
                    new ContentAccessBlob(getMiniValue(columnIndex).contentAccess()),
                    StandardCharsets.ISO_8859_1, // FIXME
                    1, // FIXME
                    StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        ResultField resultField = getResultField(columnIndex);
        Object result = resultField.get();
        wasNull = (result == null);
        return result;
    }

    public MiniValue getMiniValue(int columnIndex) throws SQLException {
        if (columnIndex < 1 || columnIndex > columnCount) {
            throw new SQLException(String.format("Invalid column index: %d", columnIndex));
        }
        
        return currentRecord.row().get(columnIndex - 1);
    }
    
    public ResultField getResultField(int columnIndex) throws SQLException {
        if (columnIndex < 1 || columnIndex > columnCount) {
            throw new SQLException(String.format("Invalid column index: %d", columnIndex));
        }
        
        return currentRecord.get(columnIndex - 1);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    // [end]
    
    
    // --- UPDATE ---
    // [start]

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(
            String columnLabel, InputStream x, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(
            String columnLabel, InputStream x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(
            String columnLabel, Reader reader, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(
            String columnLabel, InputStream x, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(
            String columnLabel, InputStream x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBinaryStream(
            int columnIndex, InputStream x, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(
            String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateBlob(
            int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw createReadOnlyException();
    }

    // [end]
    
    
    // --- ROW MANIPULATION ---
    // [start]
    
    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    // [end]

    
    // --- ROW ID ---
    // [start]
    
    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // [end]
    
    
    // --- CLOSE ---
    // [start]
    
    @Override
    public void close() throws SQLException {
        // TODO
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false; // TODO
    }
    
    // [end]

    

    private SQLException createForwardOnlyException() {
        return new SQLException("This result set is FORWARD_ONLY");
    }

    private SQLException createReadOnlyException() {
        return new SQLException("This result set is READ_ONLY");
    }

}
