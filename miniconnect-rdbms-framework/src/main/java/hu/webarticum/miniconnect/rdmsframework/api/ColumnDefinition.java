package hu.webarticum.miniconnect.rdmsframework.api;

public interface ColumnDefinition {

    public boolean isNullable();
    
    public String sqlType(); // FIXME
    
    public Class<?> javaType();
    
    // TODO: default value/generator, value restrictions
    
}
