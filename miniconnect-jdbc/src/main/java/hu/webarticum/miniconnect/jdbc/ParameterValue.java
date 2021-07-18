package hu.webarticum.miniconnect.jdbc;

import java.sql.Types;

public class ParameterValue {
    
    private final Class<?> type;
    
    private final Object value;
    
    private final int sqlType;
    
    private final String typeName;
    
    private final Object modifier;


    public ParameterValue(Class<?> type, Object value) {
        this(type, value, Types.OTHER, null, null);
    }
    
    public ParameterValue(
            Class<?> type,
            Object value,
            int sqlType,
            String typeName,
            Object modifier) {
        
        this.type = type;
        this.value = value;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.modifier = modifier;
    }


    public Class<?> type() {
        return type;
    }

    public Object value() {
        return value;
    }

    public int sqlType() {
        return sqlType;
    }

    public String typeName() {
        return typeName;
    }

    public Object modifier() {
        return modifier;
    }
    
}
