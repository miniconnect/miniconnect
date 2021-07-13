package hu.webarticum.miniconnect.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class MiniJdbcResultSetMetaData implements ResultSetMetaData {

    private final MiniJdbcResultSet resultSet;

    private final ImmutableList<DefaultValueInterpreter> interpreters; // FIXME
    
    
    public MiniJdbcResultSetMetaData(MiniJdbcResultSet resultSet) {
        this.resultSet = resultSet;
        this.interpreters = resultSet.getMiniResultSet().columnHeaders().map(
                h -> new DefaultValueInterpreter(h.valueDefinition()));
    }
    
    
    public MiniJdbcResultSet getResultSet() {
        return resultSet;
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
        return resultSet.getMiniResultSet().columnHeaders().size();
    }

    // FIXME: Default...
    public DefaultValueInterpreter getValueInterpreter(int column) {
        return interpreters.get(column - 1);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        
        // FIXME / TODO
        return Types.JAVA_OBJECT;
        
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        
        // FIXME / TODO
        return "JAVA_OBJECT";
        
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return getValueInterpreter(column).type().getName();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return resultSet.getMiniResultSet().columnHeaders().get(column - 1).name();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnLabel(column); // FIXME / TODO
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return ResultSetMetaData.columnNullableUnknown; // TODO
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return false; // TODO
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return ""; // TODO
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0; // TODO
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
        return 0; // TODO
    }

}
