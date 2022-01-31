package hu.webarticum.miniconnect.jdbcadapter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.tool.result.StoredValue;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class JdbcAdapterResultSet implements MiniResultSet {
    
    // TODO: use EnumMap with JDBCType instead -> JDBCType.valueOf(int type)
    private static final Map<Integer, Class<?>> TYPE_MAPPING =
            Collections.synchronizedMap(new HashMap<>());
    static {
        TYPE_MAPPING.put(Types.NULL, Void.class);
        TYPE_MAPPING.put(Types.BOOLEAN, Boolean.class);
        TYPE_MAPPING.put(Types.BIT, Boolean.class);
        TYPE_MAPPING.put(Types.TINYINT, Integer.class);
        TYPE_MAPPING.put(Types.SMALLINT, Integer.class);
        TYPE_MAPPING.put(Types.INTEGER, Integer.class);
        TYPE_MAPPING.put(Types.BIGINT, Long.class);
        TYPE_MAPPING.put(Types.NUMERIC, BigDecimal.class);
        TYPE_MAPPING.put(Types.DECIMAL, BigDecimal.class);
        TYPE_MAPPING.put(Types.REAL, Float.class);
        TYPE_MAPPING.put(Types.FLOAT, Double.class);
        TYPE_MAPPING.put(Types.DOUBLE, Double.class);
        TYPE_MAPPING.put(Types.BINARY, ByteString.class);
        TYPE_MAPPING.put(Types.VARBINARY, ByteString.class);
        TYPE_MAPPING.put(Types.LONGVARBINARY, ByteString.class);
        TYPE_MAPPING.put(Types.CHAR, String.class);
        TYPE_MAPPING.put(Types.NCHAR, String.class);
        TYPE_MAPPING.put(Types.VARCHAR, String.class);
        TYPE_MAPPING.put(Types.NVARCHAR, String.class);
        TYPE_MAPPING.put(Types.LONGVARCHAR, String.class);
        TYPE_MAPPING.put(Types.LONGNVARCHAR, String.class);

        TYPE_MAPPING.put(Types.TIME, LocalTime.class);
        TYPE_MAPPING.put(Types.DATE, LocalDate.class);
        TYPE_MAPPING.put(Types.TIMESTAMP, Instant.class);
        
        // FIXME
        TYPE_MAPPING.put(Types.BLOB, ByteString.class);
        TYPE_MAPPING.put(Types.CLOB, String.class);
        TYPE_MAPPING.put(Types.NCLOB, String.class);

        // TODO
        /*
        TIME_WITH_TIMEZONE
        TIMESTAMP_WITH_TIMEZONE
        JAVA_OBJECT
        SQLXML
        OTHER
        ROWID
        DISTINCT
        STRUCT
        ARRAY
        REF
        DATALINK
        REF_CURSOR
        */
        
        // TODO: more
        // TODO: settings, encodings etc.
        
    }

    private final ImmutableList<Class<?>> javaTypes;
    
    private final ImmutableList<DefaultValueInterpreter> interpreters;
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;
    
    private final Statement jdbcStatement;
    
    private final ResultSet jdbcResultSet;
    
    private final JdbcResultSetIterator<ImmutableList<MiniValue>> rowIterator;
    
    
    // FIXME extract nullability information
    public JdbcAdapterResultSet(Statement jdbcStatement, ResultSet jdbcResultSet) {
        this.jdbcStatement = jdbcStatement;
        this.jdbcResultSet = jdbcResultSet;
        this.javaTypes = extractJavaTypes();
        this.interpreters = javaTypes.map(DefaultValueInterpreter::new);
        this.columnHeaders = extractColumnHeaders();
        this.rowIterator = new JdbcResultSetIterator<>(jdbcResultSet, r -> extractRow());
    }

    
    private final ImmutableList<Class<?>> extractJavaTypes() {
        try {
            return extractJavaTypesThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private final ImmutableList<Class<?>> extractJavaTypesThrowing() throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<Class<?>> resultBuilder = new ArrayList<>(columnCount);
        for (int c = 1; c <= columnCount; c++) {
            resultBuilder.add(extractJavaTypeThrowing(jdbcMetaData, c));
        }
        return new ImmutableList<>(resultBuilder);
    }
    
    private Class<?> extractJavaTypeThrowing(
            ResultSetMetaData jdbcMetaData, int c) throws SQLException {
        
        return getJavaTypeOf(jdbcMetaData.getColumnType(c));
    }
    
    // FIXME
    private Class<?> getJavaTypeOf(int jdbcType) {
        if (!TYPE_MAPPING.containsKey(jdbcType)) {
            return ByteString.class;
        }
        
        return TYPE_MAPPING.get(jdbcType);
    }
    
    private ImmutableList<MiniColumnHeader> extractColumnHeaders() {
        try {
            return extractColumnHeadersThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private ImmutableList<MiniColumnHeader> extractColumnHeadersThrowing() throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<MiniColumnHeader> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            int c = i + 1;
            String name = jdbcMetaData.getColumnLabel(c);
            MiniValueDefinition valueDefinition = interpreters.get(i).definition();
            resultBuilder.add(new StoredColumnHeader(name, valueDefinition));
        }
        return new ImmutableList<>(resultBuilder);
    }
    

    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    @Override
    public Iterator<ImmutableList<MiniValue>> iterator() {
        return rowIterator;
    }
    
    private ImmutableList<MiniValue> extractRow()  {
        try {
            return extractRowThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private ImmutableList<MiniValue> extractRowThrowing() throws SQLException {
        // FIXME: what to do on exception?
        int columnCount = jdbcResultSet.getMetaData().getColumnCount();
        List<MiniValue> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            MiniValue value = extractValue(i);
            resultBuilder.add(value);
        }
        return new ImmutableList<>(resultBuilder);
    }

    private MiniValue extractValue(int i) {
        try {
            return extractValueThrowing(i);
        } catch (Exception e) {
            // FIXME: what to do?
            return new StoredValue();
        }
    }
    
    private MiniValue extractValueThrowing(int i) throws SQLException {
        int c = i + 1;
        DefaultValueInterpreter interpreter = interpreters.get(i);
        Class<?> mappingType = javaTypes.get(i);
        Class<?> jdbcMappingType = jdbcMappingTypeOf(mappingType);
        Object jdbcValue = jdbcResultSet.getObject(c, jdbcMappingType);
        Object value = convertJdbcValue(mappingType, jdbcValue);
        return interpreter.encode(value);
    }
    
    private Class<?> jdbcMappingTypeOf(Class<?> mappingType) {
        if (mappingType == LocalTime.class) {
            return java.sql.Time.class;
        } else if (mappingType == LocalDate.class) {
            return java.sql.Date.class;
        } else if (mappingType == Instant.class) {
            return java.sql.Timestamp.class;
        } else if (mappingType == ByteString.class) {
            return byte[].class;
        } else {
            return mappingType;
        }
    }
    
    private Object convertJdbcValue(Class<?> mappingType, Object jdbcValue) {
        if (jdbcValue == null) {
            return null;
        } else if (mappingType == LocalTime.class) {
            return ((java.sql.Time) jdbcValue).toLocalTime();
        } else if (mappingType == LocalDate.class) {
            return ((java.sql.Date) jdbcValue).toLocalDate();
        } else if (mappingType == Instant.class) {
            return ((java.sql.Timestamp) jdbcValue).toInstant();
        } else if (mappingType == ByteString.class) {
            return ByteString.wrap((byte[]) jdbcValue);
        } else {
            return jdbcValue;
        }
    }

    @Override
    public void close() {
        try {
            jdbcResultSet.close();
            jdbcStatement.close();
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException("Unexpected SQLException", e));
        }
    }

}
