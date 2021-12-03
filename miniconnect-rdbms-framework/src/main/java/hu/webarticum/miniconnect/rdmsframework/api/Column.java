package hu.webarticum.miniconnect.rdmsframework.api;

public interface Column extends NamedResource {

    public ColumnDefinition definition();
    
    public Object get(int index);
    
}
