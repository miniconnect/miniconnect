package hu.webarticum.miniconnect.jdbcadapter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.converter.Converter;
import hu.webarticum.miniconnect.record.converter.DefaultConverter;
import hu.webarticum.miniconnect.record.type.StandardValueType;
import hu.webarticum.miniconnect.record.type.ValueType;

public class JdbcAdapterResultSet implements MiniResultSet {
    
    private static final Map<JDBCType, ValueType> TYPE_MAPPING =
            Collections.synchronizedMap(new EnumMap<>(JDBCType.class));
    static {
        TYPE_MAPPING.put(JDBCType.NULL, StandardValueType.NULL);
        TYPE_MAPPING.put(JDBCType.BOOLEAN, StandardValueType.BOOL);
        TYPE_MAPPING.put(JDBCType.BIT, StandardValueType.BOOL);
        TYPE_MAPPING.put(JDBCType.TINYINT, StandardValueType.INT);
        TYPE_MAPPING.put(JDBCType.SMALLINT, StandardValueType.INT);
        TYPE_MAPPING.put(JDBCType.INTEGER, StandardValueType.INT);
        TYPE_MAPPING.put(JDBCType.BIGINT, StandardValueType.LONG);
        TYPE_MAPPING.put(JDBCType.NUMERIC, StandardValueType.DECIMAL);
        TYPE_MAPPING.put(JDBCType.DECIMAL, StandardValueType.DECIMAL);
        TYPE_MAPPING.put(JDBCType.REAL, StandardValueType.FLOAT);
        TYPE_MAPPING.put(JDBCType.FLOAT, StandardValueType.DOUBLE);
        TYPE_MAPPING.put(JDBCType.DOUBLE, StandardValueType.DOUBLE);
        TYPE_MAPPING.put(JDBCType.BINARY, StandardValueType.BINARY);
        TYPE_MAPPING.put(JDBCType.VARBINARY, StandardValueType.BINARY);
        TYPE_MAPPING.put(JDBCType.LONGVARBINARY, StandardValueType.BINARY);
        TYPE_MAPPING.put(JDBCType.CHAR, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.NCHAR, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.VARCHAR, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.NVARCHAR, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.LONGVARCHAR, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.LONGNVARCHAR, StandardValueType.STRING);

        TYPE_MAPPING.put(JDBCType.TIME, StandardValueType.TIME);
        TYPE_MAPPING.put(JDBCType.DATE, StandardValueType.DATE);
        TYPE_MAPPING.put(JDBCType.TIMESTAMP, StandardValueType.TIMESTAMP);
        
        // FIXME: use some blob wrapper
        TYPE_MAPPING.put(JDBCType.BLOB, StandardValueType.BINARY);
        TYPE_MAPPING.put(JDBCType.CLOB, StandardValueType.STRING);
        TYPE_MAPPING.put(JDBCType.NCLOB, StandardValueType.STRING);

        /*
        
        // TODO: support more types
        
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
        
        etc.
        
        settings, encodings etc.
        */
        
    }


    private final ImmutableList<ValueType> valueTypes;
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;
    
    private final Statement jdbcStatement;
    
    private final ResultSet jdbcResultSet;
    
    
    public JdbcAdapterResultSet(Statement jdbcStatement, ResultSet jdbcResultSet) {
        this.jdbcStatement = jdbcStatement;
        this.jdbcResultSet = jdbcResultSet;
        this.valueTypes = extractValueTypes(jdbcResultSet);
        this.columnHeaders = extractColumnHeaders(jdbcResultSet, valueTypes);
    }
    
    private static ImmutableList<ValueType> extractValueTypes(ResultSet jdbcResultSet) {
        try {
            return extractValueTypesThrowing(jdbcResultSet);
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private static ImmutableList<ValueType> extractValueTypesThrowing(
            ResultSet jdbcResultSet) throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<ValueType> resultBuilder = new ArrayList<>(columnCount);
        for (int c = 1; c <= columnCount; c++) {
            resultBuilder.add(extractValueTypeThrowing(jdbcMetaData, c));
        }
        return ImmutableList.fromCollection(resultBuilder);
    }
    
    private static ValueType extractValueTypeThrowing(
            ResultSetMetaData jdbcMetaData, int c) throws SQLException {
        int jdbcTypeNumber = jdbcMetaData.getColumnType(c);
        JDBCType jdbcType = JDBCType.valueOf(jdbcTypeNumber);
        
        if (jdbcType == JDBCType.DECIMAL && jdbcMetaData.getScale(c) == 0) {
            return StandardValueType.BIGINT;
        }
        
        return TYPE_MAPPING.getOrDefault(jdbcType, StandardValueType.BINARY);
    }
    
    private static ImmutableList<MiniColumnHeader> extractColumnHeaders(
            ResultSet jdbcResultSet, ImmutableList<ValueType> valueTypes) {
        try {
            return extractColumnHeadersThrowing(jdbcResultSet, valueTypes);
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private static ImmutableList<MiniColumnHeader> extractColumnHeadersThrowing(
            ResultSet jdbcResultSet, ImmutableList<ValueType> valueTypes) throws SQLException {
        ResultSetMetaData jdbcMetaData = jdbcResultSet.getMetaData();
        int columnCount = jdbcMetaData.getColumnCount();
        List<MiniColumnHeader> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            int c = i + 1;
            String name = jdbcMetaData.getColumnLabel(c);
            boolean isNullable = (jdbcMetaData.isNullable(c) != ResultSetMetaData.columnNoNulls);
            MiniValueDefinition valueDefinition =
                    valueTypes.get(i).defaultTranslator().definition();
            resultBuilder.add(new StoredColumnHeader(name, isNullable, valueDefinition));
        }
        return ImmutableList.fromCollection(resultBuilder);
    }
    

    @Override
    public ImmutableList<MiniColumnHeader> columnHeaders() {
        return columnHeaders;
    }

    @Override
    public ImmutableList<MiniValue> fetch() {
        try {
            return fetchThrowing();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    private ImmutableList<MiniValue> fetchThrowing() throws SQLException {
        if (!jdbcResultSet.next()) {
            return null;
        }
        
        return extractRowThrowing();
    }
    
    private ImmutableList<MiniValue> extractRowThrowing() throws SQLException {
        int columnCount = jdbcResultSet.getMetaData().getColumnCount();
        List<MiniValue> resultBuilder = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            MiniValue value = extractValue(i);
            resultBuilder.add(value);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private MiniValue extractValue(int i) {
        ValueType valueType = valueTypes.get(i);
        Class<?> mappingType = valueType.clazz();
        Object value = getValue(i, mappingType);
        return valueType.defaultTranslator().encodeFully(value);
    }
    
    private Object getValue(int i, Class<?> mappingType) {
        int c = i + 1;
        Class<?> jdbcMappingType = jdbcMappingTypeOf(mappingType);
        
        // TODO: handle precision/scale, use LargeInteger in case of DECIMAL(-1, 0)
        
        try {
            return convertJdbcValue(mappingType, jdbcResultSet.getObject(c, jdbcMappingType));
        } catch (SQLException e) {
            // TODO: log?
        }
        
        Object value;
        try {
            value = jdbcResultSet.getObject(c);
        } catch (SQLException e) {
            return null;
        }

        if (mappingType.isInstance(value)) {
            return value;
        }
        
        Converter converter = new DefaultConverter();
        try {
            return converter.convert(value, mappingType);
        } catch (Exception e) {
            return value;
        }
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
    
    @Override
    public boolean isClosed() {
        try {
            return jdbcResultSet.isClosed();
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException("Database access error", e));
        }
    }

}
