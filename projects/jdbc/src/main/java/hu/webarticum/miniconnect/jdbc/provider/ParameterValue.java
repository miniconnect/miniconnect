package hu.webarticum.miniconnect.jdbc.provider;

import java.sql.Types;

public class ParameterValue {
    
    private final Class<?> type;
    
    private final Object value;
    
    private final int sqlType;
    
    private final String typeName;
    
    private final Object modifier;
    
    private final boolean managed;


    public ParameterValue(Class<?> type, Object value) {
        this(type, value, null);
    }

    public ParameterValue(Class<?> type, Object value, Object modifier) {
        this(type, value, Types.OTHER, null, modifier);
    }
    
    public ParameterValue(
            Class<?> type,
            Object value,
            int sqlType,
            String typeName,
            Object modifier) {
        this(type, value, sqlType, typeName, modifier, false);
    }

    public ParameterValue(
            Class<?> type,
            Object value,
            int sqlType,
            String typeName,
            Object modifier,
            boolean managed) {
        this.type = type;
        this.value = value;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.modifier = modifier;
        this.managed = managed;
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

    public boolean managed() {
        return managed;
    }
    
}
