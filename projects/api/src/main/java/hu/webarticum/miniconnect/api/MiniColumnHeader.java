package hu.webarticum.miniconnect.api;

public interface MiniColumnHeader {

    public String name();
    
    public boolean isNullable();

    public MiniValueDefinition valueDefinition();

}
