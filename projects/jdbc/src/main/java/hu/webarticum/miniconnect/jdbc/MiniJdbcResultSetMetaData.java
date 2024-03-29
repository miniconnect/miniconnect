package hu.webarticum.miniconnect.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.record.lob.BlobValue;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class MiniJdbcResultSetMetaData implements ResultSetMetaData {

    private static final Map<String, JDBCType> TYPE_MAPPING =
            Collections.synchronizedMap(new HashMap<>());
    static {
        // FIXME
        TYPE_MAPPING.put(Void.class.getName(), JDBCType.NULL);
        TYPE_MAPPING.put(Boolean.class.getName(), JDBCType.BOOLEAN);
        TYPE_MAPPING.put(Byte.class.getName(), JDBCType.TINYINT);
        TYPE_MAPPING.put(Short.class.getName(), JDBCType.SMALLINT);
        TYPE_MAPPING.put(Integer.class.getName(), JDBCType.INTEGER);
        TYPE_MAPPING.put(Long.class.getName(), JDBCType.BIGINT);
        TYPE_MAPPING.put(Float.class.getName(), JDBCType.REAL);
        TYPE_MAPPING.put(Double.class.getName(), JDBCType.DOUBLE);
        TYPE_MAPPING.put(LargeInteger.class.getName(), JDBCType.NUMERIC);
        TYPE_MAPPING.put(BigInteger.class.getName(), JDBCType.NUMERIC);
        TYPE_MAPPING.put(BigDecimal.class.getName(), JDBCType.DECIMAL);
        TYPE_MAPPING.put(ByteString.class.getName(), JDBCType.LONGVARBINARY);
        TYPE_MAPPING.put(byte[].class.getName(), JDBCType.LONGVARBINARY);
        TYPE_MAPPING.put(Character.class.getName(), JDBCType.CHAR);
        TYPE_MAPPING.put(String.class.getName(), JDBCType.LONGVARCHAR);
        TYPE_MAPPING.put(LocalTime.class.getName(), JDBCType.TIME);
        TYPE_MAPPING.put(Time.class.getName(), JDBCType.TIME);
        TYPE_MAPPING.put(LocalDate.class.getName(), JDBCType.DATE);
        TYPE_MAPPING.put(Date.class.getName(), JDBCType.DATE);
        TYPE_MAPPING.put(java.util.Date.class.getName(), JDBCType.DATE);
        TYPE_MAPPING.put(Instant.class.getName(), JDBCType.TIMESTAMP);
        TYPE_MAPPING.put(Timestamp.class.getName(), JDBCType.TIMESTAMP);
        TYPE_MAPPING.put(OffsetTime.class.getName(), JDBCType.TIME_WITH_TIMEZONE);
        TYPE_MAPPING.put(OffsetDateTime.class.getName(), JDBCType.TIMESTAMP_WITH_TIMEZONE);
        TYPE_MAPPING.put(ZonedDateTime.class.getName(), JDBCType.TIMESTAMP_WITH_TIMEZONE);
        TYPE_MAPPING.put(BlobValue.class.getName(), JDBCType.BLOB);
        TYPE_MAPPING.put(ClobValue.class.getName(), JDBCType.CLOB);
    }
    
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<String> columnClassNames;
    
    
    public MiniJdbcResultSetMetaData(
            ImmutableList<MiniColumnHeader> columnHeaders, ImmutableList<String> columnClassNames) {
        this.columnHeaders = columnHeaders;
        this.columnClassNames = columnClassNames;
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
    public int getColumnCount() throws SQLException {
        return columnClassNames.size();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        String columnClassName = getColumnClassName(column);
        if (!TYPE_MAPPING.containsKey(columnClassName)) {
            return Types.JAVA_OBJECT;
        }
        
        return TYPE_MAPPING.get(columnClassName).getVendorTypeNumber();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        String columnClassName = getColumnClassName(column);
        if (!TYPE_MAPPING.containsKey(columnClassName)) {
            return "JAVA_OBJECT";
        }
        
        return TYPE_MAPPING.get(columnClassName).getName();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return columnClassNames.get(column - 1);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        MiniColumnHeader columnHeader = columnHeaders.get(column - 1);
        return columnHeader.name();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnLabel(column); // FIXME
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public int isNullable(int column) throws SQLException {
        MiniColumnHeader columnHeader = columnHeaders.get(column - 1);
        return columnHeader.isNullable() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return getColumnName(column).equalsIgnoreCase("id"); // FIXME heuristic
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true; // TODO
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        // FIXME: support for currencies?
        String type = columnHeaders.get(column - 1).valueDefinition().type();
        try {
            return Class.forName(type) == BigDecimal.class;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        // FIXME: support for unsigned numbers?
        String type = columnHeaders.get(column - 1).valueDefinition().type();
        try {
            return Number.class.isAssignableFrom(Class.forName(type));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return ""; // TODO
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        MiniValueDefinition valueDefinition = columnHeaders.get(column - 1).valueDefinition();
        Class<?> clazz;
        try {
            clazz = Class.forName(valueDefinition.type());
        } catch (Exception e) {
            return 0;
        }
        
        if (clazz == String.class || clazz == ByteString.class || clazz == byte[].class) {
            return Math.max(0, valueDefinition.length());
        } else if (clazz == Boolean.class || clazz == Byte.class || clazz == Character.class) {
            return 1;
        } else if (clazz == Short.class) {
            return 5;
        } else if (clazz == Integer.class) {
            return 10;
        } else if (clazz == Long.class) {
            return 19;
        } else if (clazz == Float.class) {
            return 7;
        } else if (clazz == Double.class) {
            return 15;
        } else if (clazz == LargeInteger.class || clazz == BigInteger.class || clazz == BigDecimal.class) {
            return 30; // FIXME
        } else {
            return 0; // TODO
        }
    }

    @Override
    public int getScale(int column) throws SQLException {
        MiniValueDefinition valueDefinition = columnHeaders.get(column - 1).valueDefinition();
        Class<?> clazz;
        try {
            clazz = Class.forName(valueDefinition.type());
        } catch (Exception e) {
            return 0;
        }
        if (clazz == BigDecimal.class) {
            return 3; // FIXME
        } else {
            return 0; // TODO
        }
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return ""; // TODO
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return ""; // TODO
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return !isWritable(column);
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return Math.max(
                columnHeaders.get(column - 1).name().length(),
                getPrecision(column));
    }
    
}
