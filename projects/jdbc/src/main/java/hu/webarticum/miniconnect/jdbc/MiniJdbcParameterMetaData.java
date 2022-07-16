package hu.webarticum.miniconnect.jdbc;

import java.sql.JDBCType;
import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Types;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class MiniJdbcParameterMetaData implements ParameterMetaData {
    
    private final ImmutableList<ParameterDefinition> parameters;
    
    
    public MiniJdbcParameterMetaData(ImmutableList<ParameterDefinition> parameters) {
        this.parameters = parameters;
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
    public int getParameterCount() throws SQLException {
        return parameters.size();
    }

    @Override
    public int isNullable(int param) throws SQLException {
        return ParameterMetaData.parameterNullableUnknown; // TODO
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        return false; // TODO
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int getScale(int param) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        return Types.VARCHAR; // TODO (VARCHAR is H2's default for unknown)
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        return JDBCType.VARCHAR.getName(); // TODO (VARCHAR is H2's default for unknown)
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        return String.class.getName(); // TODO (VARCHAR is H2's default for unknown)
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        return ParameterMetaData.parameterModeIn; // TODO: currently only IN parameters are supported
    }

}
