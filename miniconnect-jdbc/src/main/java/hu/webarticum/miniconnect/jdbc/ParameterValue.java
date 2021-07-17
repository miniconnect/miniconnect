package hu.webarticum.miniconnect.jdbc;

public class ParameterValue {
    
    private final Class<?> type;

    private final Object value;
    
    private final Object modifier;

    
    public ParameterValue(Class<?> type, Object value, Object modifier) {
        this.type = type;
        this.value = value;
        this.modifier = modifier;
    }


    public Class<?> type() {
        return type;
    }

    public Object value() {
        return value;
    }

    public Object modifier() {
        return modifier;
    }
    
}
